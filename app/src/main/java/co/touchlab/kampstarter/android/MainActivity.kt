package co.touchlab.kampstarter.android

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.blockstack.android.stackstransactions.message.*
import org.komputing.kbignumbers.biginteger.BigInteger
import org.openintents.blockstack.stackstransactions.Stacks
import org.openintents.blockstack.stackstransactions.StacksNetwork

class MainActivity : AppCompatActivity() {
    val senderKey = "3af1c76f27c861d7a0f3e85543c0191dff8d8b8de6d27660aadb18b7f20400a901"
    val senderAddress = "ST218TF4VFT21C7WHF72EB434HD9D237161MBJYVD"
    val nonce: Long = 0
    var account: Account? = null

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            textinput_error.text = ""
            textinput_error.visibility = View.INVISIBLE
            val receiver = recipient.text.toString()
            try {
                Address(receiver)
            } catch (e: Exception) {
                textinput_error.visibility = View.VISIBLE
                textinput_error.text = getString(R.string.invalid_address, e.localizedMessage)
                return@setOnClickListener
            }
            val amount = BigInteger.valueOf(amount.text.toString().toLong())
            Log.d(TAG, "Amount ${amount.toString(10)}")
            GlobalScope.launch(Dispatchers.IO) {
                //val acc = getAccount(senderAddress)
                //account = acc
                val transaction = Stacks.makeSTXTokenTransfer(
                    receiver,
                    amount,
                    senderKey,
                    TokenTransferOptions(
                        BigInteger.valueOf(180),
                        BigInteger.valueOf(nonce),
                        StacksNetwork.testnet,
                        memo = "Sent from Android"
                    )
                )

                try {
                    val result =
                        transaction.broadcast()

                    Log.d(TAG, result)
                } catch (cause: Throwable) {
                    withContext(Dispatchers.Main) {
                        textinput_error.visibility = View.VISIBLE
                        textinput_error.text =
                            getString(R.string.invalid_address, cause.localizedMessage)
                    }
                    Log.e(TAG, "failed to broadcast", cause)
                }
            }
        }

    }
}
