package org.blockstack.android.stackstransactions.message

import kotlin.test.Test
import kotlin.test.assertEquals


internal class LengthPrefixedStringTest {
    @Test
    fun testCodec() {
        val expected = LengthPrefixedString("ABC", 1)
        val bytes = expected.serialize()
        val actual = LengthPrefixedString(ByteArrayReader(bytes, 0), 1)
        assertEquals(expected.content, actual.content)
    }

}