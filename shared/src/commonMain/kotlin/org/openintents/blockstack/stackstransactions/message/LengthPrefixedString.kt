package org.blockstack.android.stackstransactions.message

import org.blockstack.android.stackstransactions.message.Constants.MAX_STRING_LENGTH_BYTES
import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.toHexString
import org.komputing.khex.extensions.toNoPrefixHexString


open class LengthPrefixedString(val content:String? = null, val lengthPrefixBytes:Int = 1, val maxLengthBytes: Int = MAX_STRING_LENGTH_BYTES) : StacksMessageCodec {
    @OptIn(ExperimentalStdlibApi::class)
    override fun serialize(): ByteArray {
        if (content == null) {
            error("'content' is null")
        }

        if (exceedsMaxLength(content, maxLengthBytes)) {
            error("String length exceeds maximum bytes ${this.maxLengthBytes}")
        }
        val bytes = content.encodeToByteArray()
        val lengthBytes = intToHexString(bytes.size, lengthPrefixBytes).hexToByteArray()
        return byteArrayOf(*lengthBytes, *bytes)
    }
    companion object {
        @OptIn(ExperimentalStdlibApi::class)
        operator fun invoke(reader: ByteArrayReader, lengthPrefixBytes:Int): LengthPrefixedString {
            val length = reader.read(lengthPrefixBytes).toNoPrefixHexString()
            val contentBytes = reader.read(length.toInt(16))
            return LengthPrefixedString(contentBytes.decodeToString(),
                    lengthPrefixBytes, contentBytes.size)
        }
    }
}

fun intToHexString(value: Int, lengthBytes: Int): String {
    return value.toString(16).padStart(lengthBytes * 2, '0')
}

@OptIn(ExperimentalStdlibApi::class)
fun exceedsMaxLength(content: String?, maxLengthBytes: Int): Boolean {
    return if (content != null) {
        content.encodeToByteArray().size > maxLengthBytes
    } else {
        false
    }
}
