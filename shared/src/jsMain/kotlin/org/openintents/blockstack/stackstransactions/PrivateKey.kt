package org.openintents.blockstack.stackstransactions

actual class PrivateKey actual constructor(key: String) {
  actual fun toPublicKeyString(): String {
    TODO("Not yet implemented")
  }
}

actual fun signMessageHash(
  hash: ByteArray,
  privateKey: PrivateKey
): String {
  TODO("Not yet implemented")
}
