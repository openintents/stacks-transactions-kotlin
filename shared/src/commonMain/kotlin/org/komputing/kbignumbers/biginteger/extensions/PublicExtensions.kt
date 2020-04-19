package org.komputing.kbignumbers.biginteger.extensions

import org.komputing.kbignumbers.biginteger.BigInteger
import org.komputing.khex.extensions.toNoPrefixHexString


fun BigInteger.toHexStringZeroPadded(size: Int, withPrefix: Boolean = true): String {
  var result = toByteArray().toNoPrefixHexString()

  val length = result.length
  if (length > size) {
    throw UnsupportedOperationException("Value $result is larger then length $size")
  } else if (signum() < 0) {
    throw UnsupportedOperationException("Value cannot be negative")
  }

  if (length < size) {
    result = "0".repeat(size - length) + result
  }

  return if (withPrefix) {
    "0x$result"
  } else {
    result
  }
}
