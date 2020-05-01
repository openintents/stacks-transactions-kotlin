package org.openintents.blockstack.stackstransactions

import org.blockstack.android.stackstransactions.TransactionSigner
import org.blockstack.android.stackstransactions.message.*
import org.komputing.kbignumbers.biginteger.BigInteger
import org.openintents.blockstack.stackstransactions.signature.PrivateKey

object Stacks {
    fun makeSTXTokenTransfer(
        receiverAddress: String,
        amount: BigInteger,
        feeRate: BigInteger,
        senderKey: String,
        options: TokenTransferOptions
    ): StacksTransaction {

        val nonce = options.nonce
            ?: BigInteger.ZERO
        val version = options.network?.version
            ?: TransactionVersion.Mainnet
        val memo = options.memo ?: ""
        val postConditionMode = options.postConditionMode
            ?: PostConditionMode.Deny
        val postConditions = options.postConditions
        val chainId = options.network?.chainId ?: ChainId.MainNet

        val payload = TokenTransferPayload(receiverAddress, amount, memo)

        val addressHashMode = AddressHashMode.SerializeP2PKH
        println(senderKey)
        val privKey =
            PrivateKey.fromString(senderKey)
        val spendingCondition = SingleSigSpendingCondition(
            addressHashMode,
            privKey.toPublicKeyString(),
            nonce,
            feeRate
        )
        val authorization = StandardAuthorization(spendingCondition)
        val transaction = StacksTransaction(
            version, chainId.id, authorization, payload,
            postConditionMode, LengthPrefixedList(arrayListOf())
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
        feeRate: BigInteger,
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
            feeRate
        )
        val authorization = StandardAuthorization(spendingCondition)

        val transaction = StacksTransaction(
            network.version, network.chainId.id, authorization, payload,
            postConditionMode, LengthPrefixedList(arrayListOf())
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
        feeRate: BigInteger,
        senderKey: String,
        options: ContractCallOptions
    ): StacksTransaction {

        val nonce = options.nonce ?: BigInteger.ZERO
        val network = options.network
            ?: StacksNetwork.mainnet
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
            feeRate
        )
        val authorization = StandardAuthorization(spendingCondition)


        val transaction = StacksTransaction(
            network.version, network.chainId.id, authorization, payload,
            postConditionMode, LengthPrefixedList(arrayListOf())
        )

        options.postConditions?.forEach {
            transaction.addPostCondition(it)
        }

        val signer = TransactionSigner(transaction)
        signer.signOrigin(privKey)

        return transaction
    }
}
