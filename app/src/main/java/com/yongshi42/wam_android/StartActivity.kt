package com.yongshi42.wam_android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        var warbandjson = JsonObject()
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.warbandjson.value = warbandjson

        btnnew.setOnClickListener() {
            /**
             * Create new warband and add to warbandjson
             */
            val warbandname = "test warband"
            val warbandrace = "test"
            val warbandcategory = "test"
            warbandjson.addProperty("Warband", warbandname)
            warbandjson.addProperty("Race", warbandrace)
            warbandjson.addProperty("Category", warbandcategory)
            Log.d("WAM_Android", "Created warbandjson as $warbandjson")

            /**
             * Create savefile for new warband
             */
            val filename = "$warbandname.json"
            Helpers().writeJSONtoFile(filename = filename, json = warbandjson, context = this)
            Log.d("WAM_Android", "Saved warbandjson to file with filename $filename")
        }

        btnload.setOnClickListener() {
            /**
             * get warband name to load
             */
            val warbandname = "test warband.json"
            val filename = "$warbandname.json"
            Log.d("WAM_Android", "Got filename as $filename")

            /**
             * Load warband to warbandjson
             */
            warbandjson = Helpers().readJSONfromFile(filename, this)!!
            Log.d("WAM_Android", "Loaded $warbandname as json $warbandjson")
        }
    }


}