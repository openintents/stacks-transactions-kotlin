package org.blockstack.android.stackstransactions.message


import org.komputing.kbignumbers.biginteger.BigInteger
import org.komputing.kbignumbers.biginteger.extensions.toHexStringZeroPadded
import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.toNoPrefixHexString
import org.openintents.blockstack.stackstransactions.ClarityValue
import org.openintents.blockstack.stackstransactions.serializeCV

open class Payload(
  val payloadType: PayloadType?,

  val assetType: AssetType? = null,
  val assetInfo: AssetInfo? = null,
  val assetName: LengthPrefixedString? = null,
  val recipient: Principal? = null,
  val amount: BigInteger? = null,
  val memo: MemoString? = null,

  val contractAddress: Address? = null,
  val contractName: LengthPrefixedString? = null,
  val functionName: LengthPrefixedString? = null,
  val functionArgs: Array<ClarityValue>? = null,

  val codeBody: CodeBodyString? = null,

  val coinbaseBuffer: ByteArray? = null

) : StacksMessageCodec {
  @OptIn(ExperimentalStdlibApi::class)
  override fun serialize(): ByteArray {
    if (payloadType == null) {
      error("'payloadType' not specified")
    }
    when (payloadType) {

      PayloadType.TokenTransfer -> {
        if (recipient == null) {
          error("'recipientAddress' not specified")
        }
        if (amount == null) {
          error("'amount' not specified")
        }
        if (memo == null) {
          error("'memo' not specified")
        }
        return byteArrayOf(
          payloadType.type,
          *recipient.serialize(),
          *amount.toHexStringZeroPadded(16, false).hexToByteArray(),
          *memo.serialize()
        )
      }

      PayloadType.SmartContract -> {
        if (contractName == null) {
          notSpecifiedError("contractName")
        }
        if (codeBody == null) {
          notSpecifiedError("codeBoy")
        }
        return byteArrayOf(
          payloadType.type,
          *contractName.serialize(),
          *codeBody.serialize()
        )
      }

      PayloadType.ContractCall -> {
        if (this.contractAddress == null) {
          notSpecifiedError("contractAddress")
        }
        if (this.contractName == null) {
          notSpecifiedError("contractName")
        }
        if (this.functionName == null) {
          notSpecifiedError("functionName")
        }
        if (this.functionArgs == null) {
          notSpecifiedError("functionArgs")
        }

        val argsBytes = functionArgs.map{ serializeCV(it)}
        val numberOfBytes = argsBytes.fold(0) {
            sum, it -> sum + it.size
        }

        val functionArgsBytes = ByteArray(numberOfBytes)
        var offset = 0
        for (arg in argsBytes ) {
          arg.copyInto(functionArgsBytes, offset)
          offset += arg.size
        }

        return byteArrayOf(
          payloadType.type,
          *contractAddress.serialize(),
          *contractName.serialize(),
          *functionName.serialize(),
          *intToHexString(functionArgs.size, 4).hexToByteArray(),
          *functionArgsBytes
        )
      }

      else -> {
        throw Error("Unsupported payload type")
      }
    }
  }

  companion object {
    @OptIn(ExperimentalStdlibApi::class)
    fun deserialize(reader: ByteArrayReader): Payload {
      val payloadType = PayloadType.getByValue(reader.readByte())
      return when (payloadType) {
        PayloadType.TokenTransfer -> {
          val recipientAddress = Principal(reader)
          val amount = BigInteger(reader.read(8).decodeToString())
          val memo = MemoString.deserialize(reader)
          TokenTransferPayload(recipientAddress, amount, memo)
        }
        else -> {
          error("unsupported payload type")
        }
      }
    }
  }
}

class TokenTransferPayload(recipient: Principal, amount: BigInteger, memo: MemoString) :
  Payload(
    PayloadType.TokenTransfer, recipient = recipient, amount = amount, memo = memo
  ) {
  constructor(recipientAddress: String, amount: BigInteger, memo: String?) :
    this(StandardPrincipal(Address(recipientAddress)), amount, MemoString(memo))
}

class SmartContractPayload(contractName: LengthPrefixedString, codeBody: CodeBodyString) :
  Payload(PayloadType.SmartContract, contractName = contractName, codeBody = codeBody) {
  constructor(contractName: String, codeBody: String) :
    this(LengthPrefixedString(contractName), CodeBodyString(codeBody))
}

class PoisonPayload : Payload(PayloadType.PoisonMicroblock)

class CoinbasePayload(coinbaseBuffer: ByteArray) :
  Payload(PayloadType.Coinbase, coinbaseBuffer = coinbaseBuffer)

class ContractCallPayload(
  contractAddress: String,
  contractName: String,
  functionName: String,
  functionArgs: Array<ClarityValue>
) :
  Payload(
    PayloadType.ContractCall, contractAddress = Address(contractAddress),
    contractName = LengthPrefixedString(contractName),
    functionName = LengthPrefixedString(functionName),
    functionArgs = functionArgs
  )

class MemoString(val content: String? = null) : StacksMessageCodec {
  @OptIn(ExperimentalStdlibApi::class)
  override fun serialize(): ByteArray {
    if (content == null) {
      error("'content' not defined")
    }
    return content.encodeToByteArray()
      .toNoPrefixHexString()
      .padEnd(Constants.MEMO_MAX_LENGTH_BYTES * 2, '0')
      .hexToByteArray()
  }

  companion object {
    @OptIn(ExperimentalStdlibApi::class)
    fun deserialize(reader: ByteArrayReader): MemoString {
      val content = reader.read(Constants.MEMO_MAX_LENGTH_BYTES).decodeToString()
        .dropLastHexZeros()
      return MemoString(content)
    }
  }
}

private fun String.dropLastHexZeros():String {
  for (index in lastIndex downTo 0)
    if (index % 2 == 0 && this[index] != 0.toChar())
      return substring(0, index + 1)
  return ""
}

class CodeBodyString(content: String? = null) : LengthPrefixedString(content, 4, 100000)
