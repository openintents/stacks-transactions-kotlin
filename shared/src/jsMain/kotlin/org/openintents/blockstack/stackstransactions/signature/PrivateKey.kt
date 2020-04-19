package org.openintents.blockstack.stackstransactions.signature

import org.openintents.blockstack.stackstransactions.signature.PrivateKey

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
