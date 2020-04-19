package org.blockstack.android.stackstransactions.message

import org.komputing.kbignumbers.biginteger.BigInteger

class ContractDeployOptions(val nonce: BigInteger? = null,
                            val version: TransactionVersion? = null,
                            val postConditionMode: PostConditionMode? = null,
                            val postConditions: Array<PostCondition>? = null)
