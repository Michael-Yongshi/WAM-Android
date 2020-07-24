package com.yongshi42.wam_android

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.yongshi42.wam_android.helpers.FileHelper
import com.yongshi42.wam_android.start.StartViewModel
import kotlinx.android.synthetic.main.activity_start.*


class StartActivity : AppCompatActivity() {

    private lateinit var startViewModel: StartViewModel
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        /**
         * start viewmodel for this activity and first retrieval of filelist
         */
        startViewModel = ViewModelProvider(this).get(StartViewModel::class.java)
        Log.d("startViewModel", "Started view model declaration")

        /**
         * Set the listview widget and link to view model list files
         */
        var listview: ListView = findViewById(R.id.list_files)
        startViewModel.filenames.observe(this, Observer { filenames ->
            adapter = ArrayAdapter<String>(this, R.layout.activity_start_listitem, filenames)
            listview.adapter = adapter
            Log.d("ethnfc debug", "account fragment gets accountjson found in mainviewmodel ")
        })

        /**
         * retrieve file list in viewmodel
         */
        updateListView()

        /**
         * Set listener on listview to load when name is selected
         */
        listview.setOnItemClickListener {parent, view, position, id ->

            /**
             * get warband name to load
             */
            val filename: String = adapter.getItem(position).toString()
            Toast.makeText(this, "Clicked item with position $position and name $filename",Toast.LENGTH_SHORT).show()
            Log.d("WAM_Android", "Got filename as $filename")

            /**
             * Load warband to warbandjson
             */
            startViewModel.warbandjson.value = FileHelper().readJSONfromFile(filename, this)!!
            Log.d("WAM_Android", "Loaded $filename as json ${startViewModel.warbandjson.value}")
        }

        /**
         * get the edit text view and enable it to filter items
         */
        val inputName: EditText = findViewById(R.id.nameinput)
        inputName.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                // When user changed the Text
                adapter.filter.filter(cs)
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(arg0: Editable) {
                // TODO Auto-generated method stub
            }
        })

        /**
         * New button to create a new warband based on the inputted name
         */
        btnnew.setOnClickListener() {
            /**
             * Create new warband and add to warbandjson
             */
            val nameView: EditText = findViewById(R.id.nameinput)
            val warbandname: String = nameView.text.toString()
            startViewModel.warbandjson.value = JsonObject()
            startViewModel.warbandjson.value!!.addProperty("Warband", warbandname)
            startViewModel.warbandjson.value!!.addProperty("Race", "test")
            startViewModel.warbandjson.value!!.addProperty("Category", "test")
            Log.d("WAM_Android", "Created warbandjson as ${startViewModel.warbandjson.value}")

            /**
             * Create savefile for new warband
             */
            val filename = "$warbandname.json"
            FileHelper().writeJSONtoFile(filename = filename, json = startViewModel.warbandjson.value!!, context = this)
            Log.d("WAM_Android", "Saved warbandjson to file with filename $filename")

            updateListView()
        }

        btnimport.setOnClickListener() {

        }
    }

    private fun updateListView() {
        /**
         * Get list of all files in app directory again
         */
        startViewModel.filenames.value = FileHelper().getFilenameList(this)
        Log.d("Filenames", "Filenames received: $startViewModel.filenames.value")
    }

    private fun showWirelessSettings() {
        /**
         * open wireless settings
         */
        Toast.makeText(this, "You need to enable NFC in order to scan models", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        startActivity(intent)
    }

    private fun FinishActivity() {
        /**
         * Start main activity and close this
         */
        val navintent = Intent(this@StartActivity, MainActivity::class.java)
        navintent.putExtra("warbandjson", startViewModel.warbandjson.value.toString())
        startActivity(navintent)
        finish()
    }

}