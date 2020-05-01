package org.blockstack.android.stackstransactions.message

import org.komputing.khash.ripemd160.extensions.digestRipemd160
import org.komputing.khash.sha256.extensions.sha256
import org.walleth.khex.hexToByteArray
import org.walleth.khex.toNoPrefixHexString


@OptIn(ExperimentalStdlibApi::class)
actual fun hashP2PKH(hexInput: String): String {
    val sha256Result = hexInput.hexToByteArray().sha256()
    val h160 = sha256Result.digestRipemd160().toNoPrefixHexString()
    println("***")
    println(hexInput)
    println(h160)
    println("***")
    return h160
}