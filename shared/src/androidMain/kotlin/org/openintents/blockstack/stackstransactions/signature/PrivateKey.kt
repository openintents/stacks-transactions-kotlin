package org.openintents.blockstack.stackstransactions.signature

import org.kethereum.crypto.getCompressedPublicKey
import org.kethereum.crypto.toECKeyPair
import org.komputing.khex.extensions.toNoPrefixHexString
import org.openintents.blockstack.stackstransactions.signature.PrivateKeyData.Companion.isCompressed

actual class PrivateKey (val key: org.kethereum.model.PrivateKey, val privateKeyData: PrivateKeyData) {

  actual fun toPublicKeyString(): String {
    return key.toECKeyPair().getCompressedPublicKey().toNoPrefixHexString()
  }

  actual companion object {
    actual fun fromString(key:String):PrivateKey {
      val compressed = isCompressed(key)
      return PrivateKey(org.kethereum.model.PrivateKey(key.substring(0,64)), PrivateKeyData(key, compressed))
    }
  }
}

actual fun signMessageHash(
  hash: ByteArray,
  privateKey: PrivateKey
): String {
  val key = privateKey.privateKeyData.key.substring(0, 64)
  val keyPair = org.kethereum.model.PrivateKey(key).toECKeyPair()
  val signature = org.kethereum.crypto.signMessageHash(hash, keyPair, true)
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
