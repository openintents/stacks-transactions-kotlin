package org.blockstack.android.stackstransactions.message

open class Authorization(
    val authType: AuthType?,
    val spendingCondition: SpendingCondition?
) : StacksMessageCodec {
    override fun serialize(): ByteArray {
        return byteArrayOf(
            authType?.type ?: notSpecifiedError("authType"),
            *spendingCondition?.serialize() ?: notSpecifiedError("spendingCondition")
        )
    }

    fun intoInitialSighashAuth(): Authorization {
        return if (this.authType === AuthType.Standard) {
            StandardAuthorization(this.spendingCondition?.clear())
        } else {
            return SponsoredAuthorization(this.spendingCondition?.clear())
        }
    }

    companion object {
        fun deserialize(reader: ByteArrayReader): Authorization {
            val authType = AuthType.getByValue(reader.readByte())
            val spendingCondition = SpendingCondition.deserialize(reader)
            return if (authType == AuthType.Standard)
                StandardAuthorization(spendingCondition)
            else
                SponsoredAuthorization(spendingCondition)

        }
    }
}

class StandardAuthorization(spendingCondition: SpendingCondition?) : Authorization(
    AuthType.Standard, spendingCondition
)

class SponsoredAuthorization(spendingCondition: SpendingCondition?) : Authorization(
    AuthType.Sponsored, spendingCondition
)
