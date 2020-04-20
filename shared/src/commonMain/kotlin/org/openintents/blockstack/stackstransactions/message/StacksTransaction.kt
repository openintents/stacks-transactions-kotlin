package org.blockstack.android.stackstransactions.message

import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.toNoPrefixHexString
import org.openintents.blockstack.stackstransactions.signature.PrivateKey

class StacksTransaction(val version: TransactionVersion?,
                        val chainId: String?,
                        val auth: Authorization?,
                        val payload: Payload?,
                        val postConditionMode: PostConditionMode = PostConditionMode.Deny,
                        val postConditions: LengthPrefixedList<PostCondition> = LengthPrefixedList(arrayListOf())): StacksMessageCodec {
    val anchorMode: AnchorMode

    init {
        if (payload?.payloadType == PayloadType.TokenTransfer ||
                payload?.payloadType == PayloadType.ContractCall ||
                payload?.payloadType == PayloadType.SmartContract) {
            anchorMode = AnchorMode.Any
        } else if (payload?.payloadType == PayloadType.PoisonMicroblock ||
                payload?.payloadType == PayloadType.Coinbase) {
            anchorMode = AnchorMode.OnChainOnly
        } else {
            error("unexpected transaction payload type ${payload?.payloadType}")
        }
    }

    fun addPostCondition(postCondition: PostCondition) {
        postConditions.items.add(postCondition)
    }

    override fun serialize(): ByteArray {
        if (this.version == null) {
            throw error("'version' is undefined")
        }
        if (this.chainId == null) {
            throw error("'chainId' is undefined")
        }
        if (this.auth == null) {
            throw error("'auth' is undefined")
        }
        if (this.payload == null) {
            notSpecifiedError("payload")
        }
        return byteArrayOf(
                version.version,
                *chainId.hexToByteArray(),
                *auth.serialize(),
                anchorMode.mode,
                postConditionMode.mode,
                *postConditions.serialize(),
                *payload.serialize()
        )
    }

    fun signBegin(): String {
        if (auth == null) {
            throw notSpecifiedError("auth")
        }
        val cleanTx = StacksTransaction(version, chainId,auth.intoInitialSighashAuth(), payload, postConditionMode, postConditions )
        return cleanTx.txId()
    }

    private fun txId(): String {
        val serialized = serialize()
        println(serialized.toNoPrefixHexString())
        println(txIdFromData(serialized))
        return txIdFromData(serialized)
    }

    fun signNextOrigin(sigHash: String, privKey: PrivateKey): String {

        if (this.auth == null) {
            throw notSpecifiedError("auth")
        }
        if (this.auth.spendingCondition == null) {
            throw notSpecifiedError("auth.spendingCondition")
        }
        if (this.auth.authType == null) {
            throw notSpecifiedError("auth.authType")
        }
        return this.signAndAppend(this.auth.spendingCondition, sigHash, this.auth.authType, privKey)

    }

    private fun signAndAppend(spendingCondition: SpendingCondition, curSigHash: String, authType: AuthType, privKey: PrivateKey): String {
        if (spendingCondition.feeRate == null) {
            throw  Error("\"condition.feeRate\" is undefined")
        }
        if (spendingCondition.nonce == null) {
            throw  Error("\"condition.nonce\" is undefined")
        }

        val nextSignatureData = SpendingCondition.nextSignature(
                curSigHash,
                authType,
                spendingCondition.feeRate,
                spendingCondition.nonce,
                privKey
        )
        if (spendingCondition.singleSig()) {
            spendingCondition.signature = nextSignatureData.signature
        } else {
            // condition.pushSignature();
        }

        return nextSignatureData.hash
    }
}
