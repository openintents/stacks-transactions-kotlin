package org.komputing.kbignumbers.biginteger.extensions

import org.komputing.kbignumbers.biginteger.BigInteger
import org.komputing.khex.extensions.toNoPrefixHexString


fun BigInteger.toHexStringZeroPadded(size: Int, withPrefix: Boolean = true): String {
  var result = toByteArray().toNoPrefixHexString()

  val length = result.length
  if (length > size) {
    throw UnsupportedOperationException("Value $result is larger then length $size")
  }

  if (length < size) {
    if (signum() < 0) {
      result = "f".repeat(size - length) + result
    } else {
      result = "0".repeat(size - length) + result
    }
  }

  return if (withPrefix) {
    "0x$result"
  } else {
    result
  }
}
