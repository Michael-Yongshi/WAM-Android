package com.yongshi42.wam_android.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject

class StartViewModel : ViewModel() {

    val warbandjson: MutableLiveData<JsonObject> by lazy{
        MutableLiveData<JsonObject>()
    }

    val filenames: MutableLiveData<MutableList<String>> by lazy{
        MutableLiveData<MutableList<String>>()
    }
}