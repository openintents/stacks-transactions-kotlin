package org.openintents.blockstack.stackstransactions.signature

import org.openintents.blockstack.stackstransactions.signature.PrivateKeyData.Companion.isCompressed

actual class PrivateKey(
  val ec: dynamic,
  val keyPair: dynamic,
  val privateKeyData: PrivateKeyData
) {

  actual fun toPublicKeyString(): String {
    return keyPair.getPublic(privateKeyData.compressed, "hex") as String
  }

  actual companion object {
    actual fun fromString(key: String): PrivateKey {
val compressed = org.blockstack.android.stackstransactions.message.isCompressed(key)
      val ec = js("new EC('secp256k1')")
      val keyPair = ec.keyFromPrivate(key, "hex")
      println(keyPair)
      return PrivateKey(ec, keyPair, PrivateKeyData(key, compressed))
    }
  }
}

actual fun signMessageHash(
  hash: ByteArray,
  privateKey: PrivateKey
): String {
  return "0000000000000000000000000000000000000"
}
