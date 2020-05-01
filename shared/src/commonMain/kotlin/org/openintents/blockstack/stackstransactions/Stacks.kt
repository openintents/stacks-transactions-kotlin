package org.openintents.blockstack.stackstransactions

import org.blockstack.android.stackstransactions.TransactionSigner
import org.blockstack.android.stackstransactions.message.*
import org.komputing.kbignumbers.biginteger.BigInteger
import org.openintents.blockstack.stackstransactions.signature.PrivateKey

object Stacks {
    fun makeSTXTokenTransfer(
        receiverAddress: String,
        amount: BigInteger,
        senderKey: String,
        options: TokenTransferOptions
    ): StacksTransaction {

        val fee = options.fee ?: BigInteger.ZERO
        val nonce = options.nonce
            ?: BigInteger.ZERO
        val network = options.network ?: StacksNetwork.mainnet
        val anchorMode = options.anchorMode ?: AnchorMode.Any
        val postConditionMode = options.postConditionMode
            ?: PostConditionMode.Deny
        val memo = options.memo ?: ""

        val postConditions = options.postConditions

        val payload = TokenTransferPayload(receiverAddress, amount, memo)

        val addressHashMode = AddressHashMode.SerializeP2PKH
        println(senderKey)
        val privKey =
            PrivateKey.fromString(senderKey)
        val spendingCondition = SingleSigSpendingCondition(
            addressHashMode,
            privKey.toPublicKeyString(),
            nonce,
            fee
        )
        val authorization = StandardAuthorization(spendingCondition)
        val transaction = StacksTransaction(
            network.version, authorization, payload, postConditionMode,
            LengthPrefixedList(arrayListOf()), anchorMode, network.chainId.id
        )

        postConditions?.forEach {
            transaction.addPostCondition(it)
        }

        val signer = TransactionSigner(transaction)
        signer.signOrigin(privKey)

        return transaction
    }

    fun makeSmartContractDeploy(
        contractName: String,
        codeBody: String,
        senderKey: String,
        options: ContractDeployOptions
    ): StacksTransaction {
        val fee = options.fee ?: BigInteger.ZERO
        val nonce = options.nonce ?: BigInteger.ZERO
        val network = options.network ?: StacksNetwork.mainnet
        val anchorMode = options.anchorMode ?: AnchorMode.Any
        val postConditionMode = options.postConditionMode
            ?: PostConditionMode.Deny


        val payload = SmartContractPayload(contractName, codeBody)

        val addressHashMode = AddressHashMode.SerializeP2PKH
        val privKey = PrivateKey.fromString(senderKey)
        val spendingCondition = SingleSigSpendingCondition(
            addressHashMode,
            privKey.toPublicKeyString(),
            nonce,
            fee
        )
        val authorization = StandardAuthorization(spendingCondition)

        val transaction = StacksTransaction(
            network.version, authorization, payload, postConditionMode,
            LengthPrefixedList(arrayListOf()), anchorMode, network.chainId.id
        )

        options.postConditions?.forEach {
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
        senderKey: String,
        options: ContractCallOptions
    ): StacksTransaction {
        val fee = options.fee ?: BigInteger.ZERO
        val nonce = options.nonce ?: BigInteger.ZERO
        val network = options.network
            ?: StacksNetwork.mainnet
        val anchorMode = options.anchorMode ?: AnchorMode.Any
        val postConditionMode = options.postConditionMode
            ?: PostConditionMode.Deny


        val payload = ContractCallPayload(
            contractAddress,
            contractName,
            functionName,
            functionArgs
        )


        val addressHashMode = AddressHashMode.SerializeP2PKH
        val privKey =
            PrivateKey.fromString(senderKey)
        val spendingCondition = SingleSigSpendingCondition(
            addressHashMode,
            privKey.toPublicKeyString(),
            nonce,
            fee
        )
        val authorization = StandardAuthorization(spendingCondition)


        val transaction = StacksTransaction(
            network.version, authorization, payload, postConditionMode,
            LengthPrefixedList(arrayListOf()), anchorMode, network.chainId.id
        )

        options.postConditions?.forEach {
            transaction.addPostCondition(it)
        }

        val signer = TransactionSigner(transaction)
        signer.signOrigin(privKey)

        return transaction
    }
}
