package org.openintents.blockstack.stackstransactions.signature



actual class PrivateKey actual constructor(key: String) {
  private var compressed: Boolean
  val ec:dynamic
  val keyPair:dynamic

  init {
    if (key.length == 66) {
      if (!key.endsWith("01")) {
        throw error(
                "Improperly formatted private-key hex string. 66-length hex usually " +
                        "indicates compressed key, but last byte must be == 1"
                );
      }
      compressed = true;
    } else if (key.length == 64) {
      compressed = false;
    } else {
      throw error("invalid key string")
    }
    ec = js("new EC('secp256k1')")
    keyPair = ec.keyFromPrivate(key, "hex")
    println(keyPair)
  }

  actual fun toPublicKeyString(): String {
    return keyPair.getPublic(compressed, "hex") as String
  }
}

actual fun signMessageHash(
  hash: ByteArray,
  privateKey: PrivateKey
): String {
  return "0000000000000000000000000000000000000"
}
