package org.openintents.blockstack.stackstransactions.signature

expect class PrivateKey(key: String) {
  fun toPublicKeyString(): String
}

expect fun signMessageHash(hash: ByteArray, privateKey: PrivateKey): String
