package org.blockstack.android.stackstransactions.message

import org.komputing.khash.sha512.Sha512_256
import org.komputing.khex.extensions.toNoPrefixHexString

object Constants {
    const val DEFAULT_CHAIN_ID = "00000000" // Main Stacks blockchain chain ID 0x00000000
    const val MEMO_MAX_LENGTH_BYTES = 34
    const val CLARITY_INT_SIZE = 128;
    const val COINBASE_BUFFER_LENGTH_BYTES = 32;
    const val RECOVERABLE_ECDSA_SIG_LENGTH_BYTES = 65;
    const val COMPRESSED_PUBKEY_LENGTH_BYTES = 32;
    const val UNCOMPRESSED_PUBKEY_LENGTH_BYTES = 64;
    const val MAX_STRING_LENGTH_BYTES = 128
}

fun notSpecifiedError(name: String): Nothing {
    error("'$name' not specified")
}

fun txIdFromData(data: ByteArray): String  = Sha512_256.digest(data).toNoPrefixHexString()