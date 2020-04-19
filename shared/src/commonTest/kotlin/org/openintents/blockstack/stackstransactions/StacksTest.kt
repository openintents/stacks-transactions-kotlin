package org.openintents.blockstack.stackstransactions

import org.blockstack.android.stackstransactions.message.*
import org.komputing.kbignumbers.biginteger.BigInteger
import org.komputing.khex.extensions.toNoPrefixHexString
import kotlin.test.Test
import kotlin.test.assertEquals

internal abstract class StacksTest {

    @Test
    fun makeSTXTokenTransfer() {

        val recipientAddress = "SP3FGQ8Z7JY9BWYZ5WM53E0M9NK7WHJF0691NZ159"
        val amount = BigInteger("12345")
        val feeRate = BigInteger.ZERO
        val secretKey = "edf9aee84d9b7abc145504dde6726c64f369d37ee34ded868fabd876c26570bc01"
        val memo = "test memo"

        val options = TokenTransferOptions(
                BigInteger.ZERO, TransactionVersion.Mainnet,
                memo, null, null
        )

        val transaction = Stacks.makeSTXTokenTransfer(recipientAddress, amount, feeRate, secretKey, options)

        val serialized = transaction.serialize().toNoPrefixHexString()

        val expected =
                "0000000000040015c31b8c1c11c515e244b75806bac48d1399c775000000000000000000000000000" +
                        "00000000004ae1e7a04089e596377ab4a0f74dfbae05c615a8223f1896df0f28fc334dc794f6faed38abdb" +
                        "c611a0f1816738016afa25b4478e607b4d2a58c3d07925f8e040302000000000016df0ba3e79792be7be5e" +
                        "50a370289accfc8c9e032000000000000303974657374206d656d6f0000000000000000000000000000000" +
                        "0000000000000000000"

        assertEquals(expected, serialized)
    }


    @Test
    fun makeSTXTokenTransferWithPostConditions() {
        val recipientAddress = "SP3FGQ8Z7JY9BWYZ5WM53E0M9NK7WHJF0691NZ159"
        val amount = BigInteger("12345")
        val feeRate = BigInteger.ZERO
        val secretKey = "edf9aee84d9b7abc145504dde6726c64f369d37ee34ded868fabd876c26570bc01"
        val memo = "test memo"

        val postConditions = arrayOf(
                makeStandardSTXPostCondition(
                        recipientAddress,
                        GreaterEqual,
                        BigInteger("54321")
                )
        )

        val options = TokenTransferOptions(
                memo = memo,
                postConditions = postConditions
        )

        val transaction = Stacks.makeSTXTokenTransfer(recipientAddress, amount, feeRate, secretKey, options)

        val serialized = transaction.serialize().toNoPrefixHexString()

        val expected =
                "0000000000040015c31b8c1c11c515e244b75806bac48d1399c77500000000000000000000000000000000" +
                        "00008259ea38f7ac7444e043072f046db6b47cebe0b864fa60fa193eb25b82e0d3bf67073821a57392fbd5" +
                        "148827c0b1d62bb679affacdc342cc3fa4011d4f85d0db030200000001000216df0ba3e79792be7be5e50a" +
                        "370289accfc8c9e03203000000000000d4310016df0ba3e79792be7be5e50a370289accfc8c9e032000000" +
                        "000000303974657374206d656d6f00000000000000000000000000000000000000000000000000"

        assertEquals(expected, serialized)
    }

    private fun makeStandardSTXPostCondition(recipientAddress: String, code: FungibleConditionCode, amount: BigInteger): PostCondition {
        return STXPostCondition(StandardPrincipal(Address(recipientAddress)), code, amount)
    }
}
