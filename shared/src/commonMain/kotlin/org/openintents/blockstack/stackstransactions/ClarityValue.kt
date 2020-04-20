package org.openintents.blockstack.stackstransactions

import org.blockstack.android.stackstransactions.message.ClarityType
import org.blockstack.android.stackstransactions.message.intToHexString
import org.komputing.khex.extensions.hexToByteArray

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

class IntCV : ClarityValue(ClarityType.Int)
class UIntCV : ClarityValue(ClarityType.UInt)
class StandardPrincipalCV : ClarityValue(ClarityType.PrincipalStandard)
class ContractPrincipalCV : ClarityValue(ClarityType.PrincipalContract)
class ResponseErrorCV : ClarityValue(ClarityType.ResponseErr)
class ResponseOkCV : ClarityValue(ClarityType.ResponseOk)
class ListCV : ClarityValue(ClarityType.List)
class TupleCV : ClarityValue(ClarityType.Tuple)


@OptIn(ExperimentalStdlibApi::class)
fun serializeCV(value: ClarityValue): ByteArray {
    when (value) {
        is BooleanCV -> return byteArrayOf(value.type.type)
        is BufferCV -> {
            return byteArrayOf(
                value.type.type,
                *intToHexString(value.buffer.size, 4).hexToByteArray(),
                *value.buffer
            )
        }
        else ->
            error("unsupported clarity type ${value.type}")
    }
}
