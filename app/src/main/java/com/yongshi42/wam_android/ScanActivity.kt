package com.yongshi42.wam_android

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.yongshi42.wam_android.api_stuff.APIKindaStuff
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_scan.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanActivity : AppCompatActivity() {

    /**
     * creating activity wide variables
     * specific type with ? to be able to set to null
     * using !! to use these variables in a non-nullable way
     */
    private lateinit var mainViewModel: MainViewModel

    private var nfcadapter: NfcAdapter? = null
    private var pendingintent: PendingIntent? = null

    private var nfccode: Int = 1
    private var nfctext: TextView? = null
    private var accountjson: JsonObject = JsonObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        /**
         * on creation of the app, get the textview references and nfc adapter instance.
         * if no nfc then make a toast.
         */
        nfctext = findViewById(R.id.nfcresult)
        nfcadapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcadapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        pendingintent = PendingIntent.getActivity(
            this, 0,
            Intent(this, this.javaClass)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )

        /**
         * add demo button to skip scan part completely
         */
        button_demo.setOnClickListener() {
            var demostring = "{'key': 'o4xmCg1gjf2W4fp9nky7pIuwHugeWBoKRRTBNpsfWWj8ePW7dUF0WUOIQlKu4OdzjmYHwgNVNMlB\n6SREB/JyPURmwossnqshC4bWexQh/HM=\n', 'pin': '8Unc1bLdP3gJdc+hS91ZtDlieMAbZr1OWJmij+0JQqw=', 'pubkey': '0xF3A4006e787318be2A18e65fA38A3aDc8633F4Cd'}"
            accountjson = Gson().fromJson(demostring, JsonObject::class.java)
            FinishScanActivity()
        }
    }

    /**
     * check if nfc is enabled, if not show wireless settings
     * if enabled enable the foreground scanner when app resumes
     */
    override fun onResume() {
        super.onResume()
        /**
         * Add check to ping server every time app is resumed
         */

        if (nfcadapter != null) {
            if (nfcadapter!!.isEnabled == false) {
                showWirelessSettings()
            }
            nfcadapter!!.enableForegroundDispatch(this, pendingintent, null, null)
        }
    }

    /**
     * disable the foreground nfc scanner when app is paused
     */
    override fun onPause() {
        super.onPause()
        if (nfcadapter != null) {
            nfcadapter!!.disableForegroundDispatch(this)
        }
    }

    /**
     * When new nfc tag is scanned, create a new intent to do something with it
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        resolveIntent(intent)
    }

    /**
     * resolve the nfc intent received
     */
    private fun resolveIntent(intent: Intent) {
        val action = intent.action

        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {

            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val msgs: Array<NdefMessage?>

            if (rawMsgs != null) {

                msgs = arrayOfNulls(rawMsgs.size)
                for (i in rawMsgs.indices) {
                    msgs[i] = rawMsgs[i] as NdefMessage
                }

                /**
                 * Get the byte array representation of the first ndef message
                 */
                val payload: ByteArray? = msgs[0]?.records?.get(0)?.toByteArray()
//                for (i in payload!!.iterator())
//                    Log.d("ethnfc debug", "byte $i")
//                Log.d("ethnfc debug", "---------------end")

                /**
                 * Trim the payload of NDEF specific bytes
                 */
                val length: Int? = payload?.size?.minus(1)
                val trimmedpayload = payload?.sliceArray(7..length!!)
//                for (i in encryptedkey!!.iterator())
//                    Log.d("ethnfc debug", "byte $i")
//                Log.d("ethnfc debug", "---------------end")

                /**
                 * Create a 64 encoded string of the encrypted key to transport over JSON format
                 */
                val payloadstring = Base64.encodeToString(trimmedpayload, 64)
                accountjson.addProperty("key", payloadstring)
                nfcresult.text = accountjson["key"].toString()
                Log.d("ethnfc debug", "GET key from nfc $payloadstring")

                /**
                 * ask for pin number and set a hash in the accountjson
                 */
                val pinintent = Intent(this, PinActivity::class.java)
                startActivityForResult(pinintent, nfccode)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == nfccode) {
            if (resultCode == Activity.RESULT_OK) {

                val pin: String? = data?.getStringExtra("pin")

                APIKindaStuff
                    .service
                    .getHashPin(pin.toString())
                    .enqueue(object : Callback<ResponseBody> {

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.d("ethnfc debug", "GET Throwable EXCEPTION:: ${t.message}")
                            Toast.makeText(
                                applicationContext,
                                "Failed to authenticate",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                val pinhash = response.body()?.string()
                                accountjson.addProperty("pin", pinhash)
                                pinresult.text = accountjson["pin"].toString()
                                Log.d(
                                    "ethnfc debug",
                                    "GET hash from pin: ${accountjson["pin"].toString()}"
                                )

                                APIKindaStuff
                                    .service
                                    .getAccount(accountjson)
                                    .enqueue(object : Callback<ResponseBody> {
                                        override fun onFailure(
                                            call: Call<ResponseBody>,
                                            t: Throwable
                                        ) {
                                            Log.d(
                                                "ethnfc debug",
                                                "--- :: GET Throwable EXCEPTION:: ${t.message}"
                                            )
                                        }

                                        override fun onResponse(
                                            call: Call<ResponseBody>,
                                            response: Response<ResponseBody>
                                        ) {
                                            if (response.isSuccessful) {
                                                var responsestring = response.body()?.string().toString()
                                                accountjson = Gson().fromJson(responsestring, JsonObject::class.java)
                                                Log.d("ethnfc debug", "GET public key: ${accountjson["pubkey"]}")
                                                accountresult.text = accountjson["pubkey"].toString()

                                                FinishScanActivity()
                                            }
                                        }
                                    })
                            }
                        }
                    })
            }
        }
    }

    /**
     * open wireless settings
     */
    private fun showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        startActivity(intent)
    }

    /**
     * Start main activity and close this
     */
    private fun FinishScanActivity() {

        val navintent = Intent(this@ScanActivity, MainActivity::class.java)
        navintent.putExtra("accountjson", accountjson.toString())
        startActivity(navintent)
        finish()
    }
}