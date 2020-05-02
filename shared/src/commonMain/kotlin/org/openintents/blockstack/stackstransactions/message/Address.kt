package org.blockstack.android.stackstransactions.message

import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.toNoPrefixHexString
import org.openintents.blockstack.stackstransactions.signature.PrivateKeyData.Companion.isCompressed
import org.openintents.c32checksum.C32Checksum

data class Address(
    val version: AddressVersion?,
    val data: String?
) : StacksMessageCodec {

    override fun serialize(): ByteArray {
        if (version == null) {
            throw Error("'version' not specified")
        }
        if (data == null) {
            throw Error("'data' not specified")
        }
        val paddedData = data.padEnd(20 * 2, '0')
        return byteArrayOf(
            version.version, *paddedData
                .hexToByteArray()
        )
    }

    companion object {
        operator fun invoke(c32AddressString: String): Address {
            val addressData = c32addressDecode(c32AddressString)
            return Address(addressData.version, addressData.address)
        }

        fun fromPubKeys(
            version: AddressVersion,
            hashMode: AddressHashMode,
            numSigs: Int,
            publicKeys: Array<String>
        ): Address {

            if (publicKeys.size == 0) {
                throw Error("Invalid number of public keys");
            }

            if (hashMode === AddressHashMode.SerializeP2PKH || hashMode === AddressHashMode.SerializeP2WPKH) {
                if (publicKeys.size != 1 || numSigs != 1) {
                    throw Error("Invalid number of public keys or signatures");
                }
            }

            if (hashMode === AddressHashMode.SerializeP2WPKH || hashMode === AddressHashMode.SerializeP2WSH) {
                for (i in 0 until  publicKeys.size) {
                    if (!isCompressed(publicKeys[i])) {
                        throw Error("Public keys must be compressed for segwit");
                    }
                }
            }

            when (hashMode) {
                AddressHashMode.SerializeP2PKH -> {
                    val hash = hashP2PKH(publicKeys[0])
                    return Address(version, hash)
                }
                else ->
                throw Error(
                    "Not yet implemented: address construction using public keys for hash mode: ${hashMode}"
                )
            }

        }


        operator fun invoke(reader: ByteArrayReader): Address {
            val version = AddressVersion.getByValue(reader.readByte())
            val data = reader.read(20).toNoPrefixHexString()
            return Address(version, data)
        }
    }

}


expect fun hashP2PKH(hexInput:String): String

private fun c32addressDecode(c32Address: String): AddressData {
    if (c32Address.length <= 5) {
        throw Error("Invalid c32 address: length smaller than 6")
    }
    val normalized = c32Address.substring(1).toUpperCase().replace('O', '0')
        .replace('L', '1')
        .replace('I', '1')

    val dataHex = C32Checksum.decode(normalized.substring(1)) // remove version byte
    val versionChar = c32Address[1]
    val version = C32Checksum.ALPHABET.indexOf(versionChar)
    val versionHex = version.toString(16).let {
        if (it.length == 1) {
            "0$it"
        } else {
            it
        }
    }

    val address = dataHex.substring(0, dataHex.length - 8)
    val checksum = dataHex.substring(dataHex.length - 8)
    val actualChecksum = C32Checksum.c32checksum("${versionHex}${address}")

    if (checksum != actualChecksum) {
        //throw Error("Invalid c32check string: checksum mismatch")
    }
    val addressVersion =
        AddressVersion.getByValue(version.toByte()) ?: throw Error("invalid version")
    return AddressData(
        addressVersion,
        address
    )
}

data class AddressData(val version: AddressVersion, val address: String)

