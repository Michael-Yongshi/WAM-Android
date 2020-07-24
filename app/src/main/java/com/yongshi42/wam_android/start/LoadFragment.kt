package com.yongshi42.wam_android.start

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yongshi42.wam_android.R
import com.yongshi42.wam_android.helpers.FileHelper

class LoadFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_load, container, false)

        val nameView: TextView = root.findViewById(R.id.nfckey)
        val raceView: TextView = root.findViewById(R.id.passhash)
        val typeView: TextView = root.findViewById(R.id.pubkey)
        val filenameView: TextView = root.findViewById(R.id.EventId)

        /**
         * Get list of all files in app directory
         */
        val filenames: MutableList<String>? = activity?.let { FileHelper().getFilenameList(it) }
        Log.d("Filenames", "Filenames received: $filenames")

        /**
         * Choose file and return filename
         */

        return root

    }
}