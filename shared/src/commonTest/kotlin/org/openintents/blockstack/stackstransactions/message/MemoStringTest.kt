package org.blockstack.android.stackstransactions.message

import kotlin.test.Test
import kotlin.test.assertEquals

internal class MemoStringTest {
    @Test
    fun testCodec() {
        val expected = MemoString("ABC")
        val bytes = expected.serialize()
        val actual = MemoString.deserialize(ByteArrayReader(bytes,0))
        assertEquals(expected.content, actual.content)
    }
}