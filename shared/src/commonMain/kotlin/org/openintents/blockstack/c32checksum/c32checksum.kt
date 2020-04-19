package org.openintents.c32checksum

import org.komputing.khash.sha256.extensions.sha256
import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.toNoPrefixHexString

object C32Checksum {

    const val ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"
    val base32Lookup = intArrayOf(
            0xFF, 0xFF, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
            0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
            0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
            0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
            0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
            0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
            0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF
    )
    const val hex = "0123456789abcdef"

    fun encode(bytes: ByteArray): String {
        var i = 0
        var index = 0
        var digit: Int
        var currByte: Int
        var nextByte: Int
        val base32 = StringBuilder((bytes.size + 7) * 8 / 5)

        while (i < bytes.size) {
            currByte = if (bytes[i] >= 0) bytes[i].toInt() else bytes[i] + 256

            // Is the current digit going to span a byte boundary?
            if (index > 3) {
                nextByte = if (i + 1 < bytes.size) {
                    if (bytes[i + 1] >= 0) bytes[i + 1].toInt() else bytes[i + 1] + 256
                } else {
                    0
                }

                digit = currByte and (0xFF shr index)
                index = (index + 5) % 8
                digit = digit shl index
                digit = digit or (nextByte shr 8 - index)
                i++
            } else {
                digit = currByte shr 8 - (index + 5) and 0x1F
                index = (index + 5) % 8
                if (index == 0)
                    i++
            }
            base32.append(ALPHABET[digit])
        }

        return base32.toString()
    }

    fun decode(c32input: String, minLength:Int? = null): String {

        val zeroPrefix = "^${ALPHABET[0]}*".toRegex().find(c32input)?.value
        val numLeadingZeroBytes = zeroPrefix?.length ?: 0


        var res = mutableListOf<Char>()
        var carry = 0
        var carryBits = 0
        for (i in c32input.length -1  downTo 0) {
            if (carryBits == 4) {
                res.add(0, hex[carry])
                carryBits = 0
                carry = 0
            }
            val currentCode = ALPHABET.indexOf(c32input[i]) shl carryBits
            val currentValue = currentCode + carry
            val currentHexDigit = hex[currentValue % 16]
            carryBits += 1
            carry = currentValue shr 4
            if (carry > 1 shl carryBits) {
                throw  Error("Panic error in decoding.")
            }
            res.add(0, currentHexDigit)
        }
        // one last carry
        res.add(0, hex[carry])

        if (res.size % 2 == 1) {
            res.add(0, '0')
        }

        var hexLeadingZeros = 0
        for (i in 0 until res.size) {
            if (res[i] != '0') {
                break
            } else {
                hexLeadingZeros++
            }
        }

        res = res.slice(hexLeadingZeros - (hexLeadingZeros % 2) until res.size).toMutableList()

        var hexStr = String(res.toCharArray())
        for (i  in 0 until numLeadingZeroBytes) {
            hexStr = "00${hexStr}"
        }


        if (minLength != null) {
            val count = minLength * 2 - hexStr.length
            for (i in 0 until count step 2) {
                hexStr = "00${hexStr}"
            }
        }
        return hexStr
    }

    fun c32checksum(dataHex: String): String {
        val tmpHash = dataHex.hexToByteArray().sha256()
        val dataHash = tmpHash.sha256()
        return dataHash.copyOfRange(0, 4).toNoPrefixHexString()
    }
}
