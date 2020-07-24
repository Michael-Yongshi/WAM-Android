package com.yongshi42.wam_android.helpers

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import java.io.*

//https://developer.android.com/training/data-storage/shared/documents-files

class ExternalFileHelper {
    /**
     * DONT FORGET TO ADD EXTERNAL STORAGE PERMISSIONS FOR MEDIA FILES (Video, Photos, Images, etc.)
     * for documents and other files this is not necessary (Downloads, etc.)
     */

    fun writeToMedia(relpath: String, filename: String, data: String, mimetype: String, context: Context) {
        Log.d("ExternalFileHelper", "Started writetoMedia with filename $filename and data $data")

        /**
         * relpath = Environment.DIRECTORY_PHOTOS or similar
         *
         * mimetype
         * text: text/plain
         * image: image/jpeg
         * json: application/json
         */

        try {
            val resolver: ContentResolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, mimetype)
                put(MediaStore.MediaColumns.RELATIVE_PATH, relpath)
            }

            val uri: Uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)!!

            resolver.openOutputStream(uri).use {
                Log.d("ExternalFileHelper", "Started writetoFile with filename $filename and data $data")
            }
        }
        catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }

        Log.d("ExternalFileHelper", "Finished writeToMedia")
    }

    fun readFromMedia(filename: String) {
        Log.d("ExternalFileHelper", "Started readfromMedia with filename $filename")

        Log.d("ExternalFileHelper", "Finished readFromMedia")
    }

    fun writeToFile(activity: Activity, requestcode: Int, mimetype: String, filename: String) {
        Log.d("ExternalFileHelper", "Started writetoFile")

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = mimetype
        intent.putExtra(Intent.EXTRA_TITLE, filename)
        try {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
        } catch (e: IOException) {
            Log.e("Exception", "Couldnt set default folder: $e")
        }

        startActivityForResult(activity, intent, requestcode, null)

        Log.d("ExternalFileHelper", "Finished writetoFile")
        }

    fun readFromFile(activity: Activity, requestcode: Int, mimetype: String) {
        Log.d("ExternalFileHelper", "Started readfromFile")

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = mimetype

        try {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
        } catch (e: IOException) {
            Log.e("Exception", "Couldnt set default folder $e")
        }

        startActivityForResult(activity, intent, requestcode, null)

        Log.d("ExternalFileHelper", "Finished readfromFile")
    }

    fun getFilenameList(context: Context, postfix: String): MutableList<String> {
        Log.d("ExternalFileHelper", "Started getFilenameList")

        val files: Array<File>? = getFileList(context)
        val filenames: MutableList<String> = ArrayList()

        if (files != null) {
            for (file in files)
                filenames.add(file.name)
        }

        Log.d("ExternalFileHelper", "Finished getFilenameList")
        return filenames
    }

    fun getFileList(context: Context): Array<File>? {
        Log.d("ExternalFileHelper", "Started getFileList")

        /**
         * Android 10 way of getting to the external storage
         * get directory of downloads and get the list of files within
         */
        val directory: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        Log.d("Files", "Path: $directory")
        val files: Array<File>? = directory.listFiles()

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

        Log.d("ExternalFileHelper", "Finished getFileList with files $files")
        return files
    }
}