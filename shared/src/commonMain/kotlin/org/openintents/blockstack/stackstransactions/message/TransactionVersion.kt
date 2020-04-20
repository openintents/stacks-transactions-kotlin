package org.blockstack.android.stackstransactions.message

enum class PayloadType(val type: Byte) {
    TokenTransfer(0x00),
    SmartContract(0x01),
    ContractCall(0x02),
    PoisonMicroblock(0x03),
    Coinbase(0x04);

    companion object {
        private val values = values()
        fun getByValue(type: Byte) = values.firstOrNull { it.type == type }
    }
}

enum class AnchorMode(val mode: Byte) {
    OnChainOnly(0x01),
    OffChainOnly(0x02),
    Any(0x03);

    companion object {
        private val values = values()
        fun getByValue(mode: Byte) = values.firstOrNull { it.mode == mode }
    }
}

enum class TransactionVersion(val version: Byte) {
    Mainnet(0),
    Testnet(128.toByte());

    companion object {
        private val values = values()
        fun getByValue(version: Byte) = values.firstOrNull { it.version == version }
    }
}


enum class PostConditionMode(val mode: Byte) {
    Allow(0x01),
    Deny(0x02);
    companion object {
        private val values = values()
        fun getByValue(mode: Byte) = values.firstOrNull { it.mode == mode }
    }
}


enum class PostConditionType(val type: Byte) {
    STX(0x00),
    Fungible(0x01),
    NonFungible(0x02);

    companion object {
        private val values = values()
        fun getByValue(type: Byte) = values.firstOrNull { it.type == type }
    }
}

enum class AuthType(val type: Byte) {
    Standard(0x04),
    Sponsored(0x05);

    companion object {
        private val values = values()
        fun getByValue(type: Byte) = values.firstOrNull { it.type == type }
    }
}

enum class AddressHashMode(val mode: Byte) {
    // serialization modes for public keys to addresses.
    // We support four different modes due to legacy compatibility with Stacks v1 addresses:
    /** SingleSigHashMode - hash160(public-key), same as bitcoin's p2pkh */
    SerializeP2PKH(0x00),

    /** SingleSigHashMode - hash160(multisig-redeem-script), same as bitcoin's multisig p2sh */
    SerializeP2SH(0x01),

    /** MultiSigHashMode - hash160(segwit-program-00(p2pkh)), same as bitcoin's p2sh-p2wpkh */
    SerializeP2WPKH(0x02),

    /** MultiSigHashMode - hash160(segwit-program-00(public-keys))), same as bitcoin's p2sh-p2wsh */
    SerializeP2WSH(0x03);

    companion object {
        private val values = values()
        fun getByValue(mode: Byte) = values.firstOrNull { it.mode == mode }
    }
}

enum class AddressVersion(val version: Byte) {
    MainnetSingleSig(22), // "P"
    MainnetMultiSig(20), // "M"
    TestnetSingleSig(26), // "T"
    TestnetMultiSig(21); // "N"

    companion object {
        private val values = values()
        fun getByValue(version: Byte) = values.firstOrNull { it.version == version }
    }
}

enum class PubKeyEncoding(val encoding: Byte) {
    Compressed(0x00),
    Uncompressed(0x01);

    companion object {
        private val values = values()
        fun getByValue(encoding: Byte) = values.firstOrNull { it.encoding == encoding }
    }
}

open class ConditionCode(val code: Byte) {
    companion object {
        fun getByValue(code: Byte): ConditionCode {
            return when (code) {
                Equal.code -> Equal
                Greater.code -> Greater
                GreaterEqual.code -> GreaterEqual
                Less.code -> Less
                LessEqual.code -> LessEqual
                DoesNotOwn.code -> DoesNotOwn
                Owns.code -> Owns
                else -> throw Error("Invalid condition code")
            }
        }
    }
}

sealed class FungibleConditionCode(code: Byte) : ConditionCode(code)
object Equal : FungibleConditionCode(0x01)
object Greater : FungibleConditionCode(0x02)
object GreaterEqual : FungibleConditionCode(0x03)
object Less : FungibleConditionCode(0x04)
object LessEqual : FungibleConditionCode(0x05)

sealed class NonFungibleConditionCode(code: Byte) : ConditionCode(code)
object DoesNotOwn : NonFungibleConditionCode(0x10)
object Owns : NonFungibleConditionCode(0x11)

enum class PrincipalType(val type: Byte) {
    Origin(0x01),
    Standard(0x02),
    Contract(0x03),
}

enum class AssetType(type: Int) {
    STX(0x00),
    Fungible(0x01),
    NonFungible(0x02),
}

/**
 * Type IDs corresponding to each of the Clarity value types as described here:
 * {@link https://github.com/blockstack/blockstack-core/blob/sip/sip-005/sip/sip-005-blocks-and-transactions.md#clarity-value-representation}
 */
enum class ClarityType(type: Byte) {
    Int(0x00),
    UInt(0x01),
    Buffer(0x02),
    BoolTrue(0x03),
    BoolFalse(0x04),
    PrincipalStandard(0x05),
    PrincipalContract(0x06),
    ResponseOk(0x07),
    ResponseErr(0x08),
    OptionalNone(0x09),
    OptionalSome(0x0a),
    List(0x0b),
    Tuple(0x0c)
}