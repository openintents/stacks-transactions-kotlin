package org.blockstack.android.stackstransactions.message

open class Principal(val principalType: PrincipalType?,
                     val address: Address,
                     val contractName: LengthPrefixedString) : StacksMessageCodec {

    override fun serialize(): ByteArray {
        if (principalType == null) {
            throw Error("'principalType' not defined")
        }
        return if (principalType == PrincipalType.Contract) {
            byteArrayOf(principalType.type, *address.serialize(), *contractName.serialize())
        } else {
            byteArrayOf( principalType.type, *address.serialize())
        }
    }

    companion object {
        operator fun invoke(reader: ByteArrayReader): Principal {
            val type = reader.readByte()
            val address = Address(reader)
            return when (type) {
                PrincipalType.Standard.type -> StandardPrincipal(address)
                PrincipalType.Contract.type -> {
                    val contractName = LengthPrefixedString(reader, 1)
                    ContractPrincipal(address, contractName)
                }
                else -> Principal(PrincipalType.Origin, address, LengthPrefixedString())

            }
        }
    }
}

class StandardPrincipal(address: Address)
    : Principal(PrincipalType.Standard, address, LengthPrefixedString())

class ContractPrincipal(address: Address, contractName: LengthPrefixedString)
    : Principal(PrincipalType.Contract, address, contractName)
