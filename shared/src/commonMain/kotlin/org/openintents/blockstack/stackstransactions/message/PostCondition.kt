package org.blockstack.android.stackstransactions.message

import org.komputing.kbignumbers.biginteger.BigInteger
import org.komputing.kbignumbers.biginteger.extensions.toHexStringZeroPadded
import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.toHexString
import org.openintents.blockstack.stackstransactions.BufferCV
import org.openintents.blockstack.stackstransactions.deserializedCV
import org.openintents.blockstack.stackstransactions.serializeCV

open class PostCondition(val postConditionType: PostConditionType?,
                         val principal: Principal?,
                         val conditionCode: ConditionCode?,
                         val assetInfo: AssetInfo?,
                         val assetName: BufferCV?,
                         val amount: BigInteger?) : StacksMessageCodec {

    override fun serialize(): ByteArray {
        return byteArrayOf(
                postConditionType?.type ?: throw Error("PostCondition type not specified"),
                * principal?.serializeWithPrefix() ?: throw Error("'principal' is undefined"),
                * if (postConditionType == PostConditionType.Fungible ||
                        postConditionType == PostConditionType.NonFungible) {
                    assetInfo?.serialize()
                            ?: throw Error("'assetInfo' is undefined, but required for type ${postConditionType.name}")
                } else {
                    ByteArray(0)
                },
            * if (postConditionType == PostConditionType.NonFungible) {
                    assetName?.let { serializeCV(it) }
                            ?: throw Error("'assetName' is undefined, but required for type ${postConditionType.name}")
                } else {
                    ByteArray(0)
                },
                conditionCode?.code ?: throw Error("'conditionCode' not specified"),
                * if (postConditionType == PostConditionType.STX ||
                        postConditionType == PostConditionType.Fungible) {
                    amount?.toHexStringZeroPadded(16, false)?.hexToByteArray()
                            ?: throw Error("'amount' is undefined, but required for type ${postConditionType.name}")
                } else {
                    ByteArray(0)
                }
                )
    }

    companion object {
        operator fun invoke(reader: ByteArrayReader): PostCondition {
            val postConditionType = PostConditionType.getByValue(reader.readByte())
            val principal = Principal(reader)
            val assetInfo = if (postConditionType == PostConditionType.Fungible ||
                    postConditionType == PostConditionType.NonFungible) {
                AssetInfo(reader)
            } else {
                null
            }
            val assetName = if (postConditionType == PostConditionType.NonFungible) {
                deserializedCV(reader) as BufferCV
            } else {
                null
            }
            val conditionCode = ConditionCode.getByValue(reader.readByte())
            val amount = if (postConditionType == PostConditionType.STX ||
                    postConditionType == PostConditionType.Fungible) {
                BigInteger(reader.read(8).toHexString())
            } else {
                null
            }
            return PostCondition(postConditionType, principal, conditionCode, assetInfo, assetName, amount)
        }
    }
}

class STXPostCondition(principal: Principal, conditionCode: FungibleConditionCode, amount:BigInteger)
    : PostCondition(PostConditionType.STX, principal, conditionCode, null, null, amount)


class FungiblePostCondition(
    principal: Principal,
    conditionCode: FungibleConditionCode,
    amount: BigInteger,
    assetInfo: AssetInfo
) :  PostCondition(PostConditionType.Fungible, principal, conditionCode, assetInfo, null, amount)


@OptIn(ExperimentalStdlibApi::class)
class NonFungiblePostCondition(
    principal: Principal,
    conditionCode: NonFungibleConditionCode,
    assetInfo: AssetInfo,
    tokenAssetName: String
) :  PostCondition(PostConditionType.NonFungible, principal, conditionCode, assetInfo, BufferCV(tokenAssetName.encodeToByteArray()), null)