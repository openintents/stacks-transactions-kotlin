package org.openintents.blockstack.stackstransactions.signature

data class PrivateKeyData(val key: String, val compressed:Boolean) {
  companion object {
    fun isCompressed(key: String): Boolean {
      return when (key.length) {
        66 -> {
          if (!key.endsWith("01")) {
            throw error(
              "Improperly formatted private-key hex string. 66-length hex usually " +
                      "indicates compressed key, but last byte must be == 1"
            )
          }
          true
        }
        64 -> {
          false
        }
        else -> {
          throw error("invalid key string")
        }
      }
    }
  }
}

expect class PrivateKey {
  fun toPublicKeyString(): String

  companion object {
    fun fromString(key: String): PrivateKey
  }
}


expect fun signMessageHash(hash: ByteArray, privateKey: PrivateKey): String
