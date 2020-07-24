package com.yongshi42.wam_android.helpers

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONStringer

class JsonHelper {

    fun convertfromJsonObjecttoJsonString(json: JsonObject): String {
        Log.d("FileHelper", "Started convert json to string with json $json")

        val string: String = json.toString()

        Log.d("FileHelper", "Finished convert json to string with string $string")
        return string
    }

    fun convertfromJsonStringtoJsonObject(string: String): JsonObject {
        Log.d("FileHelper", "Started convert string to json with string $string")

        val json: JsonObject = Gson().fromJson(string, JsonObject::class.java)

        Log.d("FileHelper", "Finished convert string to json with json $json")
        return json
    }

}