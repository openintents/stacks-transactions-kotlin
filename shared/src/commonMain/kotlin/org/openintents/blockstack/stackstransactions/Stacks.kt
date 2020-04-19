package org.openintents.blockstack.stackstransactions

import org.blockstack.android.stackstransactions.TransactionSigner
import org.blockstack.android.stackstransactions.message.*
import org.komputing.kbignumbers.biginteger.BigInteger
import org.openintents.blockstack.stackstransactions.signature.PrivateKey

object Stacks {
  fun makeSTXTokenTransfer(
    recipientAddress: String,
    amount: BigInteger,
    feeRate: BigInteger,
    senderKey: String,
    options: TokenTransferOptions
  ): StacksTransaction {

    val normalizedOptions = TokenTransferOptions(
      options.nonce
        ?: BigInteger.ZERO,
      options.version
        ?: TransactionVersion.Mainnet,
      options.memo ?: "",
      options.postConditionMode
        ?: PostConditionMode.Deny,
      options.postConditions
    )

    val payload = TokenTransferPayload(recipientAddress, amount, normalizedOptions.memo)

    val addressHashMode = AddressHashMode.SerializeP2PKH
    val privKey =
        PrivateKey(
            senderKey.let { if (it.length == 66) it.substring(0, 64) else it })
    val spendingCondition = SingleSigSpendingCondition(
      addressHashMode,
      privKey.toPublicKeyString(),
      normalizedOptions.nonce,
      feeRate
    )
    val authorization = StandardAuthorization(spendingCondition)

    val transaction = StacksTransaction(
      normalizedOptions.version!!, Constants.DEFAULT_CHAIN_ID, authorization, payload,
      normalizedOptions.postConditionMode!!, LengthPrefixedList(arrayListOf())
    )

    normalizedOptions.postConditions?.forEach {
      transaction.addPostCondition(it)
    }

    val signer = TransactionSigner(transaction)
    signer.signOrigin(privKey)

    return transaction
  }

  fun makeSmartContractDeploy(
    contractName: String,
    codeBody: String,
    feeRate: BigInteger,
    senderKey: String,
    options: ContractDeployOptions
  ): StacksTransaction {
    val normalizedOptions = ContractDeployOptions(
      options.nonce ?: BigInteger.ZERO,
      options.version
        ?: TransactionVersion.Mainnet,
      options.postConditionMode
        ?: PostConditionMode.Deny
    )

    var payload = SmartContractPayload(contractName, codeBody)

    val addressHashMode = AddressHashMode.SerializeP2PKH
    val privKey =
        PrivateKey(
            senderKey.let { if (it.length == 66) it.substring(0, 64) else it })
    val spendingCondition = SingleSigSpendingCondition(
      addressHashMode,
      privKey.toPublicKeyString(),
      normalizedOptions.nonce,
      feeRate
    )
    val authorization = StandardAuthorization(spendingCondition)


    val transaction = StacksTransaction(
      normalizedOptions.version!!, Constants.DEFAULT_CHAIN_ID, authorization, payload,
      normalizedOptions.postConditionMode!!, LengthPrefixedList(arrayListOf())
    )

    normalizedOptions.postConditions?.forEach {
      transaction.addPostCondition(it)
    }

    val signer = TransactionSigner(transaction)
    signer.signOrigin(privKey)

    return transaction
  }

  fun makeContractCall(
    contractAddress: String,
    contractName: String,
    functionName: String,
    functionArgs: Array<ClarityValue>,
    feeRate: BigInteger,
    senderKey: String,
    options: ContractCallOptions
  ): StacksTransaction {

    val normalizedOptions = ContractCallOptions(
      options.nonce ?: BigInteger.ZERO,
      options.version
        ?: TransactionVersion.Mainnet,
      options.postConditionMode
        ?: PostConditionMode.Deny
    )

    val payload = ContractCallPayload(
      contractAddress,
      contractName,
      functionName,
      functionArgs
    )


    val addressHashMode = AddressHashMode.SerializeP2PKH
    val privKey =
        PrivateKey(
            senderKey.let { if (it.length == 66) it.substring(0, 64) else it })
    val spendingCondition = SingleSigSpendingCondition(
      addressHashMode,
      privKey.toPublicKeyString(),
      normalizedOptions.nonce,
      feeRate
    )
    val authorization = StandardAuthorization(spendingCondition)


    val transaction = StacksTransaction(
      normalizedOptions.version!!, Constants.DEFAULT_CHAIN_ID, authorization, payload,
      normalizedOptions.postConditionMode!!, LengthPrefixedList(arrayListOf())
    )

    normalizedOptions.postConditions?.forEach {
      transaction.addPostCondition(it)
    }

    val signer = TransactionSigner(transaction)
    signer.signOrigin(privKey)

    return transaction
  }
}
