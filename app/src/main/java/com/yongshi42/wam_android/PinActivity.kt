package com.yongshi42.wam_android

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class PinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        val txtPinEntry: EditText = findViewById(R.id.enterpin)
        val btnconfirm = findViewById<Button>(R.id.confirm)

        btnconfirm.setOnClickListener {
            val pinstring: String = txtPinEntry.text.toString()
//            Log.d("ethnfc debug", "pin: $pinstring")

            val returnintent = Intent().apply {putExtra("pin", pinstring)}
            setResult(Activity.RESULT_OK, returnintent)
            finish()
        }
    }
}