package org.blockstack.android.stackstransactions.message

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.receive
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpStatement
import io.ktor.http.ContentType
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.stringFromUtf8Bytes
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import org.komputing.khex.extensions.hexToByteArray
import org.komputing.khex.extensions.toNoPrefixHexString
import java.net.URL

const val STACKS_API_URL = "https://crashy-stacky.zone117x.com"
const val TAG = "broadcast"

@OptIn(ExperimentalStdlibApi::class)
actual suspend fun StacksTransaction.broadcast(): String {
    Log.d(TAG, serialize().toNoPrefixHexString())
    val client = OkHttpClient.Builder().build()
    val url = URL("$STACKS_API_URL/v2/transactions")
    val body = RequestBody.create(MediaType.parse("application/octet-stream"), serialize())
    val request = Request.Builder()
        .header("Content-Type", "application/octet-stream")
        .header("referrer", "no-referrer")
        .header("referrerPolicy", "no-referrer")
        .url(url)
        .post(body)
        .build()

    val response = withContext(Dispatchers.IO) {
        client.newCall(request).execute()
    }
    return response.body()?.string() ?:""
}

data class Account(val nonce: Long, val amount: org.komputing.kbignumbers.biginteger.BigInteger)

suspend fun getAccount(address: String): Account {
    val client = HttpClient()
    val statement = client.get<HttpStatement>("$STACKS_API_URL/v2/accounts/$address") {
        header("referrer", "no-referrer")
        header("referrerPolicy", "no-referrer")
        accept(ContentType("application", "json"))
    }
    val result = statement.execute {
        val channel = it.receive<ByteReadChannel>()
        val byteArray = ByteArray(it.content.availableForRead)
        channel.readAvailable(byteArray, 0, byteArray.size)
        JSONObject(stringFromUtf8Bytes(byteArray))
    }
    return Account(
        result.getLong("nonce"),
        org.komputing.kbignumbers.biginteger.BigInteger(
            1,
            result.getString("balance").hexToByteArray()
        )
    )
}