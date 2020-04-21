package org.blockstack.android.stackstransactions.message

import kotlin.test.Test
import kotlin.test.assertEquals


internal class AssetInfoTest {
    @Test
    fun testCodec() {
        val expected = AssetInfo("SP2JXKMSH007NPYAQHKJPQMAQYAD90NQGTVJVQ02B", "token", "stackarossi")
        val result = expected.serialize()
        val actual = AssetInfo(ByteArrayReader(result, 0))
        assertEquals(expected.address.data, actual.address.data)
        assertEquals(expected.address.version, actual.address.version)
        assertEquals(expected.contractName.content, actual.contractName.content)
        assertEquals(expected.assetName.content, actual.assetName.content)
    }
}