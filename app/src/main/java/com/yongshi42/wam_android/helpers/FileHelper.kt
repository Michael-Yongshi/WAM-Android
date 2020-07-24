package com.yongshi42.wam_android.helpers

import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import java.io.*

class FileHelper {

    fun writeJSONtoFile(filename: String, json: JsonObject, context: Context) {
        Log.d("FileHelper", "Started writeJSONtoFile with filename $filename and json $json")

        val string: String = JsonHelper().convertfromJsonObjecttoJsonString(json)
        writeToFile(filename, string, context)

        Log.d("FileHelper", "Finished writeJSONtoFile")
    }

    fun readJSONfromFile(filename: String, context: Context): JsonObject? {
        Log.d("FileHelper", "Started readJSONfromFile with filename $filename")

        val string: String? = readFromFile(filename, context)
        val json: JsonObject? = string?.let { JsonHelper().convertfromJsonStringtoJsonObject(it) }

        Log.d("FileHelper", "Finished readJSONfromFile and returning json $json")
        return json

    }

    fun writeToFile(filename: String, string: String, context: Context) {
        Log.d("FileHelper", "Started writetoFile with filename $filename and string $string")

        try {
            val outputStreamWriter = OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(string);
            outputStreamWriter.close();
        }
        catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }

        Log.d("FileHelper", "Finished writetoFile")
    }

    fun readFromFile(filename: String, context: Context): String? {
        Log.d("FileHelper", "Started readfromFile with filename $filename")

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

        Log.d("FileHelper", "Finished readfromFile and returning string $readString")
        return readString
    }

    fun getFilenameList(context: Context): MutableList<String> {
        Log.d("FileHelper", "Started getFilenameList")

        val files: Array<File>? = getFileList(context)
        val filenames: MutableList<String> = ArrayList()

        if (files != null) {
            for (file in files)
                filenames.add(file.name)
        }

        Log.d("FileHelper", "Finished getFilenameList with filenames $filenames")
        return filenames
    }

    fun getFileList(context: Context): Array<File>? {
        Log.d("FileHelper", "Started getFileList")

        /**
         * Android 10 way of getting to the external storage
         * get directory of downloads and get the list of files within
         */
        val directory: String = context.filesDir.toString()
        Log.d("Files", "Path: $directory")
        val files: Array<File>? = File(directory).listFiles()

        /**
         * iterate over the files and print the name
         */
        if (files != null) {
            Log.d("Files", "Size: ${files.size}")
            for (file in files)
                Log.d("Files", "FileName: ${file.name}")
        }

        /**
         * Return the list of files
         */

        Log.d("FileHelper", "Finished getFileList with files $files")
        return files
    }

}
