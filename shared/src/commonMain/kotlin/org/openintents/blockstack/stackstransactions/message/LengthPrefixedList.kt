package org.blockstack.android.stackstransactions.message

import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.toNoPrefixHexString

class LengthPrefixedList<T : StacksMessageCodec>(val items: MutableList<T>, val lengthPrefixBytes: Int = 4) : StacksMessageCodec {

    override fun serialize(): ByteArray {
        val itemBytes = items.map{ it.serialize()}
        val numberOfBytes = itemBytes.fold(0) {
            sum, it -> sum + it.size
        }
        val lengthPrefix = intToHexString(items.size, lengthPrefixBytes).hexToByteArray()

        val bytes = ByteArray(lengthPrefix.size + numberOfBytes)
        lengthPrefix.copyInto(bytes, 0, 0)
        var offset = lengthPrefix.size
        for (item in itemBytes ) {
            item.copyInto(bytes, offset)
            offset += item.size
        }
        return bytes
    }
}
