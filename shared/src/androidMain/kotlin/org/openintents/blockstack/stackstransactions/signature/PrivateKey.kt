package org.openintents.blockstack.stackstransactions.signature

import org.kethereum.crypto.getCompressedPublicKey
import org.kethereum.crypto.toECKeyPair
import org.komputing.khex.extensions.toNoPrefixHexString

actual class PrivateKey actual constructor(key:String) {
  val key: org.kethereum.model.PrivateKey = org.kethereum.model.PrivateKey(key)

  actual fun toPublicKeyString(): String {
    return key.toECKeyPair().getCompressedPublicKey().toNoPrefixHexString()
  }
}

actual fun signMessageHash(
  hash: ByteArray,
  privateKey: PrivateKey
): String {
  val signature = org.kethereum.crypto.signMessageHash(hash, privateKey.key.toECKeyPair(), true)
  val recoveryParam =  signature.v - java.math.BigInteger.valueOf(27L)
  val coordinateValueBytes = 32
  val signatureString = recoveryParam.toHexStringZeroPadded(2, false) +
    signature.r.toHexStringZeroPadded(coordinateValueBytes * 2, false) +
    signature.s.toHexStringZeroPadded(coordinateValueBytes * 2, false)
  return signatureString
}

fun java.math.BigInteger.toHexStringNoPrefix(): String = toString(16)
fun java.math.BigInteger.toHexStringZeroPadded(size: Int, withPrefix: Boolean = true): String {
  var result = toHexStringNoPrefix()

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
