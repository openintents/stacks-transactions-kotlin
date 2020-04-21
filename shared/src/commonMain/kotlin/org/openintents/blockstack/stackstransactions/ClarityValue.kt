package org.openintents.blockstack.stackstransactions

import org.blockstack.android.stackstransactions.message.*
import org.komputing.kbignumbers.biginteger.BigInteger
import org.komputing.kbignumbers.biginteger.extensions.toHexStringZeroPadded
import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.toNoPrefixHexString

sealed class ClarityValue(val type: ClarityType)

sealed class BooleanCV(type: ClarityType) : ClarityValue(type)
object TrueCV : BooleanCV(ClarityType.BoolTrue)
object FalseCV : BooleanCV(ClarityType.BoolFalse)

sealed class OptionalCV(type: ClarityType) : ClarityValue(type)
object NoneCV : OptionalCV(ClarityType.OptionalNone)
class SomeCV(val value: ClarityValue) : OptionalCV(ClarityType.OptionalSome)

fun optionalCVOf(value: ClarityValue?) = if (value != null) SomeCV(
    value
) else NoneCV

class BufferCV(val buffer: ByteArray) : ClarityValue(ClarityType.Buffer) {
    init {
        if (buffer.size > 1000000) {
            error("Cannot construct clarity buffer that is greater than 1MB")
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
class IntCV(value: BigInteger) : ClarityValue(ClarityType.Int) {
    val twos: ByteArray

    init {
        twos = value.toHexStringZeroPadded(32, false).hexToByteArray()
        if (twos.size > Constants.CLARITY_INT_SIZE) {
            error("Cannot construct clarity integer from value greater than INT_SIZE bits")
        }
    }

    constructor(stringValue: String) : this(BigInteger(stringValue))
    constructor(longValue:Long): this(BigInteger.valueOf(longValue))
}

class UIntCV(value: BigInteger) : ClarityValue(ClarityType.UInt) {
    val twos: ByteArray

    init {
        if (value < BigInteger.ZERO) {
            error("Cannot construct clarity integer from negative value")
        }
        twos = value.toHexStringZeroPadded(32, false).hexToByteArray()
        if (twos.size > Constants.CLARITY_INT_SIZE) {
            error("Cannot construct clarity integer from value greater than INT_SIZE bits")
        }
    }

    constructor(stringValue: String) : this(BigInteger(stringValue))
    constructor(longValue: Long):this(BigInteger.valueOf(longValue))
}

class StandardPrincipalCV(val address: Address) :
    ClarityValue(ClarityType.PrincipalStandard) {
    constructor(addressString: String) : this(Address(addressString))
}

@OptIn(ExperimentalStdlibApi::class)
class ContractPrincipalCV(val address: Address, val contractName: LengthPrefixedString) :
    ClarityValue(ClarityType.PrincipalContract) {

    init {
        if (contractName.content?.encodeToByteArray()?.size ?: 0 >= 128) {
            error("Contract name must be less than 128 bytes")
        }
    }

    constructor(addressString: String, contractName: String) : this(
        Address(addressString),
        LengthPrefixedString(contractName)
    )

    constructor(standardPrincipal: StandardPrincipalCV, contractName: String) : this(
        standardPrincipal.address,
        LengthPrefixedString(contractName)
    )
}

class ResponseErrorCV(val value: ClarityValue) : ClarityValue(ClarityType.ResponseErr)
class ResponseOkCV(val value: ClarityValue) : ClarityValue(ClarityType.ResponseOk)
class ListCV(val list: Array<ClarityValue>) : ClarityValue(ClarityType.List)
class TupleCV(val data: Map<String, ClarityValue>) : ClarityValue(ClarityType.Tuple) {
    init {
        for (key in data.keys) {
            if (!isClarityName(key)) {
                error("\"${key}\" is not a valid Clarity name")
            }
        }
    }


}

fun isClarityName(name: String): Boolean {
    val regex = "^[a-zA-Z]([a-zA-Z0-9]|[-_!?+<>=/*])*$|^[-+=/*]$|^[<>]=?$".toRegex()
    return regex.matches(name) && name.length < 128
}


@OptIn(ExperimentalStdlibApi::class)
fun serializeCV(value: ClarityValue): ByteArray {
    return when (value) {
        is BooleanCV -> byteArrayOf(value.type.type)
        is BufferCV -> {
            byteArrayOf(
                value.type.type,
                *intToHexString(value.buffer.size, 4).hexToByteArray(),
                *value.buffer
            )
        }
        is ContractPrincipalCV -> {
            byteArrayOf(
                value.type.type,
                *value.address.serialize(),
                *value.contractName.serialize()
            )
        }
        is IntCV -> {
            byteArrayOf(
                value.type.type,
                *value.twos
            )
        }
        is ListCV -> {
            byteArrayOf(
                value.type.type,
                *intToHexString(value.list.size, 4).hexToByteArray(),
                *mutableListOf<Byte>().apply {
                    for (item in value.list) {
                        val serializedValue = serializeCV(item)
                        this.addAll(serializedValue.toTypedArray())
                    }
                }.toByteArray()
            )
        }
        is NoneCV -> {
            byteArrayOf(
                value.type.type
            )
        }
        is ResponseErrorCV -> {
            byteArrayOf(
                value.type.type,
                *serializeCV(value.value)
            )
        }
        is ResponseOkCV -> {
            byteArrayOf(
                value.type.type,
                *serializeCV(value.value)
            )
        }
        is SomeCV -> {
            byteArrayOf(
                value.type.type,
            *serializeCV(value.value)
            )
        }
        is StandardPrincipalCV -> {
            byteArrayOf(
                value.type.type,
                *value.address.serialize()
            )
        }
        is TupleCV -> {
            byteArrayOf(
                value.type.type,
                *intToHexString(value.data.size, 4).hexToByteArray(),
                *mutableListOf<Byte>().apply {
                  val lexicographicOrder = value.data.keys.sorted()
                    for (key in lexicographicOrder) {
                        this.addAll(LengthPrefixedString(key).serialize().toTypedArray())
                        val serializedValue = serializeCV(value.data.getValue(key))
                        this.addAll(serializedValue.toTypedArray())
                    }
                }.toByteArray()
            )
        }
        is UIntCV -> {
            byteArrayOf(
                value.type.type,
                *value.twos
            )
        }
    }
}
