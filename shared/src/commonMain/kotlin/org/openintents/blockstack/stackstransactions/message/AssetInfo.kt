package org.blockstack.android.stackstransactions.message

class AssetInfo(var address: Address, var contractName: LengthPrefixedString,
                var assetName: LengthPrefixedString) : StacksMessageCodec {

    override fun serialize(): ByteArray {
        return byteArrayOf(*address.serialize(), *contractName.serialize(), *assetName.serialize())
    }

    companion object {
        operator fun invoke(address: String, contractName: String?, assetName: String?): AssetInfo {
            return AssetInfo(Address(address), LengthPrefixedString(contractName), LengthPrefixedString(assetName))
        }
        operator fun invoke(reader: ByteArrayReader): AssetInfo {
            val address = Address(reader)
            val contractName = LengthPrefixedString(reader, 1)
            val assetName = LengthPrefixedString(reader, 1)
            return AssetInfo(address, contractName, assetName)
        }
    }
}
