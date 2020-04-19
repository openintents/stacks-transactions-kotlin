package org.komputing.khash.sha256.extensions

import org.komputing.khash.sha256.Sha256

/**
 * Returns the SHA256 digest of this byte array.
 */
fun ByteArray.sha256() = Sha256.digest(this)

/**
 * Returns the SHA256 digest of this string.
 */
@OptIn(ExperimentalStdlibApi::class)
fun String.sha256() = this.encodeToByteArray().sha256()
