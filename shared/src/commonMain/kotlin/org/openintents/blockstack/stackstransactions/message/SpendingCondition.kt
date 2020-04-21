package org.blockstack.android.stackstransactions.message

import org.blockstack.android.stackstransactions.message.Constants.RECOVERABLE_ECDSA_SIG_LENGTH_BYTES
import org.komputing.kbignumbers.biginteger.BigInteger
import org.komputing.kbignumbers.biginteger.extensions.toHexStringZeroPadded
import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.toNoPrefixHexString
import org.openintents.blockstack.stackstransactions.signature.PrivateKey
import org.openintents.blockstack.stackstransactions.signature.signMessageHash

class MessageSignature(val signature: String?) : StacksMessageCodec {
    override fun serialize(): ByteArray {
        return signature?.hexToByteArray() ?: error("'signature' not specified")
    }

    companion object {
        fun deserialize(reader: ByteArrayReader): MessageSignature {
            return MessageSignature(
                reader.read(RECOVERABLE_ECDSA_SIG_LENGTH_BYTES).toNoPrefixHexString()
            )
        }

        fun empty(): MessageSignature {
            return MessageSignature(ByteArray(Constants.RECOVERABLE_ECDSA_SIG_LENGTH_BYTES).toNoPrefixHexString())
        }
    }
}

open class SpendingCondition(
    val addressHashMode: AddressHashMode?,
    val signerAddress: Address?,
    val nonce: BigInteger?,
    val feeRate: BigInteger?,
    val pubKeyEncoding: PubKeyEncoding?,
    var signature: MessageSignature? = MessageSignature.empty(),
    val signaturesRequired: Int?
) : StacksMessageCodec {

    @OptIn(ExperimentalStdlibApi::class)
    override fun serialize(): ByteArray {
        return byteArrayOf(
            addressHashMode?.mode ?: error("'addressHashMode' not specified"),
            *signerAddress?.data?.padEnd(20 * 2, '0')?.hexToByteArray() ?: error("'signerAddress' not specified"),
            *nonce?.toZeroPaddedHexString(8)?.hexToByteArray() ?: error("'nonce' not specified"),
            *feeRate?.toZeroPaddedHexString(8)?.hexToByteArray()
                ?: error("'feeRate' not specified"),
            pubKeyEncoding?.encoding ?: error("'pubKeyEncoding' not specified"),
            *signature?.serialize() ?: error("'signature' not specified")
        )
    }

    fun numSignatures(): Int {
        return 0
    }

    fun clear(): SpendingCondition {
        return SpendingCondition(
            addressHashMode, signerAddress?.copy(),
            BigInteger.ZERO, BigInteger.ZERO,
            pubKeyEncoding,
            signature = MessageSignature.empty(),
            signaturesRequired = signaturesRequired
        )
    }

    fun singleSig(): Boolean = this.addressHashMode?.singleSig() ?: notSpecifiedError("addressHashMode")


    companion object {
        @OptIn(ExperimentalStdlibApi::class)
        fun deserialize(reader: ByteArrayReader): SpendingCondition {
            val addressHashMode = AddressHashMode.getByValue(reader.readByte()) ?:error("Invalid address hash mode")
            return SpendingCondition(
                addressHashMode,
                Address(if (addressHashMode.singleSig()) AddressVersion.MainnetSingleSig else AddressVersion.MainnetMultiSig,
                    reader.read(20).toNoPrefixHexString()),
                BigInteger(1, reader.read(8)),
                BigInteger(1, reader.read(8)),
                PubKeyEncoding.getByValue(reader.readByte()),
                MessageSignature.deserialize(reader),
                null
            )
        }

        fun nextSignature(
            curSigHash: String,
            authType: AuthType,
            feeRate: BigInteger,
            nonce: BigInteger,
            privateKey: PrivateKey
        ): NextSignatureData {
            val sigHashPreSign = this.makeSigHashPreSign(curSigHash, authType, feeRate, nonce)
            val signatureString =
                signMessageHash(
                    sigHashPreSign.hexToByteArray(),
                    privateKey
                )
            val publicKey = privateKey.toPublicKeyString()
            val nextSigHash = this.makeSigHashPostSign(sigHashPreSign, publicKey, signatureString)

            return NextSignatureData(MessageSignature(signatureString), nextSigHash)
        }

        private fun makeSigHashPostSign(
            curSigHash: String,
            publicKey: String,
            signature: String
        ): String {
            val hashLength = 32 + 1 + RECOVERABLE_ECDSA_SIG_LENGTH_BYTES
            val pubKeyEncoding = if (!publicKey.startsWith("04")) {
                PubKeyEncoding.Compressed
            } else {
                PubKeyEncoding.Uncompressed
            }

            val sigHash =
                curSigHash + intToHexString(pubKeyEncoding.encoding.toInt(), 1) + signature
            if (sigHash.hexToByteArray().size != hashLength) {
                throw Error("Invalid signature hash length")
            }

            return txIdFromData(sigHash.hexToByteArray())
        }

        private fun makeSigHashPreSign(
            curSigHash: String,
            authType: AuthType,
            feeRate: BigInteger,
            nonce: BigInteger
        ): String {
            val hashLength = 32 + 1 + 8 + 8
            val sigHash =
                curSigHash +
                        byteArrayOf(authType.type).toNoPrefixHexString() +
                        feeRate.toHexStringZeroPadded(16, false) +
                        nonce.toHexStringZeroPadded(16, false)
            if (sigHash.hexToByteArray().size != hashLength) {
                throw Error("Invalid signature hash length")
            }

            return txIdFromData(sigHash.hexToByteArray())

        }
    }
}

data class NextSignatureData(val signature: MessageSignature, val hash: String)

class SingleSigSpendingCondition(
    addressHashMode: AddressHashMode, pubKey: String,
    nonce: BigInteger?, feeRate: BigInteger
) : SpendingCondition(
    addressHashMode,
    Address.fromPubKey(pubKey),
    nonce,
    feeRate,
    PubKeyEncoding.Compressed,
    signaturesRequired = 1
)
