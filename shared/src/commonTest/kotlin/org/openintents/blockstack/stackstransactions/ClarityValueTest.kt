package org.openintents.blockstack.stackstransactions

import org.komputing.kbignumbers.biginteger.BigInteger
import org.komputing.khex.extensions.toNoPrefixHexString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith


class ClarityValueTest {

    @Test
    fun intCV() {
        val value =  IntCV(1)
        val serialized = serializeCV(value).toNoPrefixHexString()
        assertEquals("0000000000000000000000000000000001",serialized)
    }

    @Test
    fun intCVMinusOne() {
        val value =  IntCV(-1)
        val serialized = serializeCV(value).toNoPrefixHexString()
        assertEquals("00ffffffffffffffffffffffffffffffff",serialized)
    }

    @Test
    fun uintCV() {
        val value =  UIntCV(1)
        val serialized = serializeCV(value).toNoPrefixHexString()
        assertEquals("0100000000000000000000000000000001",serialized)
    }

    @Test
    fun uintCVMinusOne() {
        assertFailsWith<IllegalStateException> {
            UIntCV(-1)
        }
    }

    @Test
    fun bufferCV() {
        val value =  BufferCV(byteArrayOf(0xde.toByte(), 0xad.toByte(), 0xbe.toByte(), 0xef.toByte()))
        val serialized = serializeCV(value).toNoPrefixHexString()
        assertEquals("0200000004deadbeef",serialized)
    }
    @Test
    fun trueCV() {
        val value =  TrueCV
        val serialized = serializeCV(value).toNoPrefixHexString()
        assertEquals("03",serialized)
    }

    @Test
    fun falseCV() {
        val value =  FalseCV
        val serialized = serializeCV(value).toNoPrefixHexString()
        assertEquals("04",serialized)
    }
    @Test
    fun noneCV() {
        val none =  NoneCV
        val serialized = serializeCV(none).toNoPrefixHexString()
        assertEquals("09",serialized)
    }

    @Test
    fun someCV() {
        val some =  SomeCV(IntCV(BigInteger.valueOf(1)))
        val serialized = serializeCV(some).toNoPrefixHexString()
        assertEquals("0a0000000000000000000000000000000001",serialized)
    }
}