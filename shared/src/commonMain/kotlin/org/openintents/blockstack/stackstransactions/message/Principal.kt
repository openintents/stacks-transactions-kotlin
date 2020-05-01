package org.blockstack.android.stackstransactions.message

open class Principal(val principalType: PrincipalType?,
                     val address: Address,
                     val contractName: LengthPrefixedString) : StacksMessageCodec {

    override fun serialize(): ByteArray {
        return serialize(true)
    }

    fun serializeWithPrefix(): ByteArray {
        return serialize(false)
    }

    fun serialize(asType:Boolean):ByteArray {
        if (principalType == null) {
            throw Error("'principalType' not defined")
        }
        return if (principalType == PrincipalType.Contract) {
            val prefix = if (asType) ClarityType.PrincipalContract.type else principalType.type
            byteArrayOf(prefix, *address.serialize(), *contractName.serialize())
        } else {
            val prefix = if (asType) ClarityType.PrincipalStandard.type else principalType.type
            byteArrayOf( prefix, *address.serialize())
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