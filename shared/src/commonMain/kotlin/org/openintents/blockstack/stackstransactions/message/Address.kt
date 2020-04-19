package org.blockstack.android.stackstransactions.message

import org.komputing.khash.ripemd160.extensions.digestRipemd160
import org.komputing.khash.sha256.extensions.sha256
import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.toNoPrefixHexString
import org.openintents.c32checksum.C32Checksum

data class Address(var version: AddressVersion?,
                   var data: String?) : StacksMessageCodec {

    override fun serialize(): ByteArray {
        if (version == null) {
            throw Error("'version' not specified")
        }
        if (data == null) {
            throw Error("'data' not specified")
        }
        return byteArrayOf(version!!.version, *data!!.hexToByteArray())
    }

    companion object {
        operator fun invoke(c32AddressString: String): Address {
            val addressData = c32addressDecode(c32AddressString)
            return Address(addressData.version, addressData.address)
        }

        fun fromPubKey(pubKey: String): Address {
            val sha256Result = pubKey.hexToByteArray().sha256().toNoPrefixHexString().hexToByteArray()
            return Address(AddressVersion.TestnetSingleSig,
                    sha256Result.digestRipemd160().toNoPrefixHexString())
        }


        operator fun invoke(reader: ByteArrayReader): Address {
            val version = AddressVersion.getByValue(reader.readByte())
            val data = reader.read(20).toNoPrefixHexString()
            return Address(version, data)
        }
    }

}

private fun c32addressDecode(c32Address: String): AddressData {
    if (c32Address.length <= 5) {
        throw Error("Invalid c32 address: invalid length");
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
        }   else {
            it
        }
    }

    val address = dataHex.substring(0, dataHex.length - 8)
    val checksum = dataHex.substring(dataHex.length-8)
    val actualChecksum = C32Checksum.c32checksum("${versionHex}${address}")

    if (checksum != actualChecksum) {
        throw Error("Invalid c32check string: checksum mismatch")
    }
    val addressVersion = AddressVersion.getByValue(version.toByte()) ?: throw Error("invalid version")
    return AddressData(addressVersion,
            address )
}

data class AddressData(val version: AddressVersion, val address: String)

