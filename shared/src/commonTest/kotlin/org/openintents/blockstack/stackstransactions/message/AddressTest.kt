package org.openintents.blockstack.stackstransactions.message

import org.blockstack.android.stackstransactions.message.Address
import org.blockstack.android.stackstransactions.message.ByteArrayReader
import kotlin.test.Test
import kotlin.test.assertEquals


internal class AddressTest {
    @Test
    fun testCodec() {
        val expected = Address("SP2JXKMSH007NPYAQHKJPQMAQYAD90NQGTVJVQ02B")
        val bytes = expected.serialize()
        val actual = Address(ByteArrayReader(bytes, 0))
        assertEquals(expected.data, actual.data)
        assertEquals(expected.version, actual.version)
    }

}