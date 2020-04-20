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

    @Test
    fun makeContractDeployTransaction() {
        val contractName = "kv-store"
        val codeBody="(define-map store ((key (buff 32))) ((value (buff 32))))\n" +
                "\n" +
                "(define-public (get-value (key (buff 32)))\n" +
                "    (match (map-get? store ((key key)))\n" +
                "        entry (ok (get value entry))\n" +
                "        (err 0)))\n" +
                "\n" +
                "(define-public (set-value (key (buff 32)) (value (buff 32)))\n" +
                "    (begin\n" +
                "        (map-set store ((key key)) ((value value)))\n" +
                "        (ok 'true)))\n"
        val feeRate = BigInteger.ZERO
        val secretKey = "e494f188c2d35887531ba474c433b1e41fadd8eb824aca983447fd4bb8b277a801"

        val options = ContractDeployOptions(
            version = TransactionVersion.Testnet
        )

        val transaction = Stacks.makeSmartContractDeploy(contractName, codeBody, feeRate, secretKey, options)

        val serialized = transaction.serialize().toNoPrefixHexString()

        val expected =
        "80000000000400e6c05355e0c990ffad19a5e9bda394a9c500342900000000000000000000000000000000" +
                "000073d449aa44ede1bc30c757ccf6cf6119f19567728be8a7d160c188c101e4ad79654f5f2345723c962f" +
                "5a465ad0e22a4237c456da46194945ae553d366eee9c4b03020000000001086b762d73746f726500000156" +
                "28646566696e652d6d61702073746f72652028286b657920286275666620333229292920282876616c7565" +
                "202862756666203332292929290a0a28646566696e652d7075626c696320286765742d76616c756520286b" +
                "65792028627566662033322929290a20202020286d6174636820286d61702d6765743f2073746f72652028" +
                "286b6579206b65792929290a2020202020202020656e74727920286f6b20286765742076616c756520656e" +
                "74727929290a20202020202020202865727220302929290a0a28646566696e652d7075626c696320287365" +
                "742d76616c756520286b65792028627566662033322929202876616c75652028627566662033322929290a" +
                "2020202028626567696e0a2020202020202020286d61702d7365742073746f72652028286b6579206b6579" +
                "292920282876616c75652076616c75652929290a2020202020202020286f6b2027747275652929290a"

        assertEquals(expected, serialized)
    }
}
