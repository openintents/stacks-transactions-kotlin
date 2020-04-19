package org.openintents.blockstack.stackstransactions

import org.blockstack.android.stackstransactions.message.ClarityType


sealed class ClarityValue

sealed class BooleanCV(type: ClarityType) : ClarityValue()
object TrueCV : BooleanCV(ClarityType.BoolTrue)
object FalseCV : BooleanCV(ClarityType.BoolFalse)

sealed class OptionalCV(type: ClarityType) : ClarityValue()
object NoneCV: OptionalCV(ClarityType.OptionalNone)
class SomeCV(val value: ClarityValue): OptionalCV(ClarityType.OptionalSome)
fun optionalCVOf(value: ClarityValue?) = if (value != null) SomeCV(
  value
) else NoneCV

class BufferCV : ClarityValue()
class IntCV : ClarityValue()
class UIntCV : ClarityValue()
class StandardPrincipalCV : ClarityValue()
class ContractPrincipalCV : ClarityValue()
class ResponseErrorCV : ClarityValue()
class ResponseOkCV : ClarityValue()
class ListCV : ClarityValue()
class TupleCV : ClarityValue()

