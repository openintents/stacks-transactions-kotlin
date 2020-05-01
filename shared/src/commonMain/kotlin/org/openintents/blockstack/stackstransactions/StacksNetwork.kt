package org.openintents.blockstack.stackstransactions

import org.blockstack.android.stackstransactions.message.ChainId
import org.blockstack.android.stackstransactions.message.TransactionVersion

data class StacksNetwork (val version: TransactionVersion, val chainId: ChainId, val coreApiUrl: String,
val broadcastApiUrl: String, val transferFeeEstimateApiUrl: String) {
    companion object {
        private val mainNetCoreApiUrl = "https://core.blockstack.org"
        private val testNetCoreApiUrl = "http://neon.blockstack.org:20443"

        val mainnet = StacksNetwork(TransactionVersion.Mainnet, ChainId.MainNet, mainNetCoreApiUrl,
        "${mainNetCoreApiUrl}/v2/transactions", "${mainNetCoreApiUrl}/v2/fees/transfer")
        val testnet = StacksNetwork(TransactionVersion.Testnet, ChainId.Testnet, testNetCoreApiUrl,
            "${testNetCoreApiUrl}/v2/transactions", "${testNetCoreApiUrl}/v2/fees/transfer")
    }
}