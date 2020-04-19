package org.blockstack.android.stackstransactions.message

/**
 * By convention classes implementing StacksMessageCodes must have
 * a constructor taking a ByteArrayReader
 */
interface StacksMessageCodec {
    fun serialize(): ByteArray
}

data class ByteArrayReader (val byteArray: ByteArray, var index:Int) {
    fun readByte(incrementIndex:Boolean = true): Byte {
        val value = byteArray[index]
        if (incrementIndex) {
            index += 1
        }
        return value
    }

    fun read(count: Int, incrementIndex:Boolean = true): ByteArray {
        val bytes = byteArray.sliceArray(index until index + count)
        if (incrementIndex) {
            index += count
        }
        return bytes
    }
}