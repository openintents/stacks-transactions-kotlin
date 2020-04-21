package org.blockstack.android.stackstransactions.message

import org.komputing.kbignumbers.biginteger.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals


internal class AuthorizationTest {
    @Test
    fun testCodec() {
        val expected = StandardAuthorization(SingleSigSpendingCondition(AddressHashMode.SerializeP2PKH,
        "03ef788b3830c00abe8f64f62dc32fc863bc0b2cafeb073b6c8e1c7657d9c2c3ab",
                BigInteger.ZERO, BigInteger.ZERO
        ))
        val bytes = expected.serialize()
        val actual = Authorization.deserialize(ByteArrayReader(bytes, 0))
        assertEquals(expected.authType, actual.authType)
        assertEquals(expected.spendingCondition?.addressHashMode, actual.spendingCondition?.addressHashMode)
        assertEquals(expected.spendingCondition?.feeRate, actual.spendingCondition?.feeRate)
        assertEquals(expected.spendingCondition?.nonce, actual.spendingCondition?.nonce)
        assertEquals(expected.spendingCondition?.pubKeyEncoding, actual.spendingCondition?.pubKeyEncoding)
        assertEquals(expected.spendingCondition?.signature?.signature, actual.spendingCondition?.signature?.signature)
        assertEquals(expected.spendingCondition?.signerAddress?.data, actual.spendingCondition?.signerAddress?.data)
    }
}