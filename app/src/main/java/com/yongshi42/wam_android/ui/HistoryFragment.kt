package com.yongshi42.wam_android.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.yongshi42.wam_android.MainViewModel
import com.yongshi42.wam_android.R
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.nav_header_main.*

class HistoryFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_history, container, false)

        val histView: TextView = root.findViewById(R.id.text_history)
        mainViewModel.historyjson.observe(viewLifecycleOwner, Observer {
            histView.text = it.toString()
        })

        val table = root.findViewById(R.id.table_history) as TableLayout
        mainViewModel.historyjson.observe(viewLifecycleOwner, Observer {

            val historyjson: JsonObject? = mainViewModel.historyjson.value?.asJsonObject
            val keys: List<String>? = historyjson?.keySet()?.toList()

            if (keys != null) {
                for (i in keys) {
                    val row = TableRow(activity)
                    val lp = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT)
                    row.layoutParams = lp

                    val eventid = TextView(activity)
                    eventid.text = historyjson[i].asJsonObject.get("Event ID").toString()
                    val challenger_stance = TextView(activity)
                    challenger_stance.text = historyjson[i].asJsonObject.get("Challenger Stance").toString()
                    val opponent_stance = TextView(activity)
                    opponent_stance.text = historyjson[i].asJsonObject.get("Opponent Stance").toString()
                    val winner = TextView(activity)
                    winner.text = historyjson[i].asJsonObject.get("Winner").toString()

                    row.addView(eventid)
                    row.addView(challenger_stance)
                    row.addView(opponent_stance)
                    row.addView(winner)
                    table.addView(row)
                }
            }

        })

        return root
    }
}