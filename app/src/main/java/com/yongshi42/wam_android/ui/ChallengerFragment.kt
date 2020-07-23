package com.yongshi42.wam_android.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import com.yongshi42.wam_android.MainViewModel
import com.yongshi42.wam_android.R
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

class ChallengerFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    private var imageView: ImageView? = null
    private val QRcodeWidth = 350
    private var bitmap: Bitmap? = null
    private var tv_qr_readTxt: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_challenger, container, false)

        /**
         * Add viewpager to slide through your characters and selecting them when in focus
         */

        val idView: TextView = root.findViewById(R.id.id)
        val nameView: TextView = root.findViewById(R.id.name)

        /**
         * Adds an observer for challengerjson, once it is retrieved from the api it updates this fragment with new data
         */
        mainViewModel.challengerjson.observe(viewLifecycleOwner, Observer { newChallenger ->

            Log.d("ethnfc debug", "challenger fragment gets challengerjson found in mainviewmodel ${mainViewModel} as ${mainViewModel.challengerjson.value.toString()}")
            idView.text = newChallenger?.get("Character ID").toString()
            nameView.text = newChallenger?.get("Name").toString()

            /**
             * Set QR code to pubkey
             */
            imageView = root.findViewById<View>(R.id.imageView) as ImageView
            tv_qr_readTxt = root.findViewById<View>(R.id.tv_qr_readTxt) as TextView

            if (idView.text.isEmpty() == false) {
                try {
                    bitmap = TextToImageEncode(idView.text as String)
                    imageView!!.setImageBitmap(bitmap)
                } catch (e: WriterException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    activity,
                    "Couldnt draw QR code",
                    Toast.LENGTH_LONG
                ).show()
            }

        })

        return root
    }

    /**
     * Creating a QR code image
     */
    @Throws(WriterException::class)
    private fun TextToImageEncode(Value: String?): Bitmap? {
        val bitMatrix: BitMatrix
        bitMatrix = try {
            MultiFormatWriter().encode(
                Value,
                BarcodeFormat.QR_CODE,
                QRcodeWidth, QRcodeWidth, null
            )
        } catch (Illegalargumentexception: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth: Int = bitMatrix.width
        val bitMatrixHeight: Int = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] =
                    if (bitMatrix.get(x, y)) resources.getColor(R.color.colorBlack)
                    else resources.getColor(R.color.colorWhite)
            }
        }
        val bitmap: Bitmap =
            Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }
}