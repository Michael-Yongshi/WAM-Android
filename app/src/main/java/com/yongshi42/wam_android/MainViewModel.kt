package com.yongshi42.wam_android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject

class MainViewModel : ViewModel() {

    val warbandjson: MutableLiveData<JsonObject> by lazy{
        MutableLiveData<JsonObject>()
    }

    val accountjson: MutableLiveData<JsonObject> by lazy{
        MutableLiveData<JsonObject>()
    }

    val charactersjson: MutableLiveData<JsonObject> by lazy{
        MutableLiveData<JsonObject>()
    }

    val challengerjson: MutableLiveData<JsonObject> by lazy{
        MutableLiveData<JsonObject>()
    }

    val opponentjson: MutableLiveData<JsonObject> by lazy{
        MutableLiveData<JsonObject>()
    }

    val historyjson: MutableLiveData<JsonObject> by lazy{
        MutableLiveData<JsonObject>()
    }

    val activeduel: MutableLiveData<JsonObject> by lazy{
        MutableLiveData<JsonObject>()
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is a data point in the main view model"
    }
    val text: LiveData<String> = _text

}