package org.openintents.blockstack.c32checksum

import org.openintents.c32checksum.C32Checksum
import kotlin.test.*

internal abstract class C32ChecksumTest {

    val hexStrings = arrayOf(
            "a46ff88886c2ef9762d970b4d2c63678835bd39d",
            "",
            "0000000000000000000000000000000000000000",
            "0000000000000000000000000000000000000001",
            "1000000000000000000000000000000000000001",
            "1000000000000000000000000000000000000000",
            "1",
            "22",
            "001",
            "0001",
            "00001",
            "000001",
            "0000001",
            "00000001",
            "10",
            "100",
            "1000",
            "10000",
            "100000",
            "1000000",
            "10000000",
            "100000000"
    )

    val c32Strings = arrayOf(
            "MHQZH246RBQSERPSE2TD5HHPF21NQMWX",
            "",
            "00000000000000000000",
            "00000000000000000001",
            "20000000000000000000000000000001",
            "20000000000000000000000000000000",
            "1",
            "12",
            "01",
            "01",
            "001",
            "001",
            "0001",
            "0001",
            "G",
            "80",
            "400",
            "2000",
            "10000",
            "G0000",
            "800000",
            "4000000"
    )

    val hexMinLengths = arrayOf(
    null,
    null,
    20,
    20,
    20,
    20,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null,
    null
    )

    @Test
    fun decode() {
        for (i in 0 until hexStrings.size) {
            val actual = C32Checksum.decode(c32Strings[i], hexMinLengths[i])
            val paddedHexString = if (hexStrings[i].length % 2 == 0 ) hexStrings[i]  else "0${hexStrings[i]}"

            assertEquals(paddedHexString, actual)

            val actualPadded = C32Checksum.decode(c32Strings[i], actual.length / 2 + 5)
            assertEquals(actualPadded, "0000000000${paddedHexString}")

            val actualNoLength = C32Checksum.decode(c32Strings[i])
            assertEquals(actualNoLength, paddedHexString)

        }
    }

}
