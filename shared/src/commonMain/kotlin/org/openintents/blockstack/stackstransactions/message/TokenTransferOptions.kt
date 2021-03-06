package org.blockstack.android.stackstransactions.message

import org.komputing.kbignumbers.biginteger.BigInteger
import org.openintents.blockstack.stackstransactions.StacksNetwork

data class TokenTransferOptions(
    val fee: BigInteger? = null,
    val nonce: BigInteger? = null,
    val network: StacksNetwork? = null,
    val anchorMode: AnchorMode? = null,
    val memo: String? = null,
    val postConditionMode: PostConditionMode? = null,
    val postConditions: Array<PostCondition>? = null
)
