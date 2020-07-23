package com.yongshi42.wam_android.ui

import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.camera.core.CameraX
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.yongshi42.wam_android.MainViewModel
import com.yongshi42.wam_android.R
import com.yongshi42.wam_android.api_stuff.APIKindaStuff
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import okhttp3.ResponseBody
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class OpponentFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel =
            ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_opponent, container, false)

        val idView: EditText = root.findViewById(R.id.id)
        val nameView: TextView = root.findViewById(R.id.name)

        val button = root.findViewById<Button>(R.id.button)

        button.setOnClickListener(View.OnClickListener {
            Log.d("ethnfc debug", "opponent fragment confirms opponent id with ${idView.text}")

            /**
             * add your public key to be able to request from the node the opponent data
             */
            var requestopponentjson: JsonObject = JsonObject()
            requestopponentjson.addProperty("pubkey", mainViewModel.accountjson.value?.get("pubkey")?.asString)
            requestopponentjson.addProperty("id", idView.text.toString())

            /**
             * request opponent data
             */
            APIKindaStuff
                .service
                .getCharacterDetails(requestopponentjson)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(
                        call: Call<ResponseBody>,
                        t: Throwable
                    ) {
                        Log.d("ethnfc debug", "--- :: GET Throwable EXCEPTION:: ${t.message}")
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            val responsestring = response.body()?.string().toString()
                            Log.d("ethnfc debug", "GET opponent: $responsestring")
                            mainViewModel.opponentjson.value = Gson().fromJson(responsestring, JsonObject::class.java)

                            /**
                             * add challenger and opponent id to request json
                             */
                            var requesthistoryjson: JsonObject = JsonObject()
                            requesthistoryjson.addProperty("pubkey", mainViewModel.accountjson.value?.get("pubkey")?.asString)
                            requesthistoryjson.addProperty("challenger_id", mainViewModel.challengerjson.value?.get("Character ID")?.asString)
                            requesthistoryjson.addProperty("opponent_id", mainViewModel.opponentjson.value?.get("Character ID")?.asString)
                            Log.d("ethnfc debug", "GET history request data: $requesthistoryjson")

                            /**
                             * request history data
                             */
                            APIKindaStuff
                                .service
                                .getHistory(requesthistoryjson)
                                .enqueue(object : Callback<ResponseBody> {
                                    override fun onFailure(
                                        call: Call<ResponseBody>,
                                        t: Throwable
                                    ) {
                                        Log.d("ethnfc debug", "--- :: GET Throwable EXCEPTION:: ${t.message}")
                                    }

                                    override fun onResponse(
                                        call: Call<ResponseBody>,
                                        response: Response<ResponseBody>
                                    ) {
                                        if (response.isSuccessful) {
                                            val responsestring = response.body()?.string().toString()
                                            Log.d("ethnfc debug", "GET history: $responsestring")
                                            mainViewModel.historyjson.value = Gson().fromJson(responsestring, JsonObject::class.java)
                                        }
                                    }
                                })
                        }
                    }
                })
        })

        /**
         * Adds an observer for opponentjson, once it is retrieved from the api it updates this fragment with new data
         */
        mainViewModel.opponentjson.observe(viewLifecycleOwner, Observer<JsonObject> { newOpponent ->
            Log.d("ethnfc debug", "opponent fragment gets opponentjson found in mainviewmodel ${mainViewModel} as ${mainViewModel.opponentjson.value.toString()}")
            nameView.text = newOpponent?.get("Name").toString()
        })


//        /**
//         * Launch camera to scan qr code
//         */
//        val scanView: TextView = root.findViewById(R.id.scan_content)
//        CameraX.initialize(this)
//
//        val imageAnalysis = ImageAnalysis.Builder()
//            .setTargetResolution(Size(1280, 720))
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .build()
//
//        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), YourImageAnalyzer())

        return root
    }

//    private class YourImageAnalyzer : ImageAnalysis.Analyzer {
//
//        override fun analyze(imageProxy: ImageProxy) {
//            val mediaImage = imageProxy.image
//            if (mediaImage != null) {
//                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//                val options: BarcodeScannerOptions = BarcodeScannerOptions.Builder()
//                    .setBarcodeFormats(
//                        Barcode.FORMAT_QR_CODE,
//                        Barcode.FORMAT_AZTEC
//                    )
//                    .build()
//
//                val scanner: BarcodeScanner = BarcodeScanning.getClient(options)
//                val result = scanner.process(image)
//                    .addOnSuccessListener { barcodes ->
//                        for (barcode in barcodes) {
//                            val bounds = barcode.boundingBox
//                            val corners = barcode.cornerPoints
//                            val rawValue = barcode.rawValue
//                            val valueType = barcode.valueType
//
//                            Log.d("ethnfc debug", "opponent fragment scans barcode resulting in $rawValue")
//                        }
//                    }
//                    .addOnFailureListener {
//                        // Task failed with an exception
//                        // ... toast
//                    }
//            }
//        }
//    }

}