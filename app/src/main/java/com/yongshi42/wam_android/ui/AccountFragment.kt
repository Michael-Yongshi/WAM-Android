package com.yongshi42.wam_android.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.yongshi42.wam_android.MainActivity
import com.yongshi42.wam_android.MainViewModel
import com.yongshi42.wam_android.R
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_scan.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.nav_header_main.*

class AccountFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_account, container, false)

        val nfckeyView: TextView = root.findViewById(R.id.nfckey)
        val passhashView: TextView = root.findViewById(R.id.passhash)
        val pubkeyView: TextView = root.findViewById(R.id.pubkey)

        /**
         * Adds an observer for charactersjson, once it is retrieved from the api it updates this fragment with new data
         */

        mainViewModel.accountjson.observe(viewLifecycleOwner, Observer { newAccount ->

            Log.d("ethnfc debug", "account fragment gets accountjson found in mainviewmodel ${mainViewModel} as ${mainViewModel.accountjson.value.toString()}")

            nfckeyView.text = newAccount?.get("key").toString()
            passhashView.text = newAccount?.get("pin").toString()
            pubkeyView.text = newAccount?.get("pubkey").toString()

        })

        return root
    }

}