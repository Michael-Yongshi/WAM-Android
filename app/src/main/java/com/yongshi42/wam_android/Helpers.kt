package com.yongshi42.wam_android

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import org.json.JSONStringer
import java.io.*

class Helpers {

    fun writeJSONtoFile(filename: String, json: JsonObject, context: Context) {
        val string: String = convertfromJsonObjecttoJsonString(json)
        writeToFile(filename, string, context)
    }

    fun readJSONfromFile(filename: String, context: Context): JsonObject? {
        val string: String? = readFromFile(filename, context)
        val json: JsonObject? = string?.let { convertfromJsonStringtoJsonObject(it) }
        return json
    }

    private fun convertfromJsonObjecttoJsonString(json: JsonObject): String {
        val string: String = Gson().fromJson(json, JSONStringer::class.java).toString()
        return string
    }

    private fun convertfromJsonStringtoJsonObject(string: String): JsonObject {
        val json: JsonObject = Gson().fromJson(string, JsonObject::class.java)
        return json
    }

    private fun writeToFile(filename: String, data: String, context: Context) {
        try {
            val outputStreamWriter = OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (e: IOException) {
            Log.e("Exception", "File write failed: $e");
        }
    }

    private fun readFromFile(filename: String, context: Context): String? {
        var readString = ""
        try {
            val inputStream: InputStream? = context.openFileInput(filename)
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                readString = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: $e")
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: $e")
        }
        return readString
    }
}