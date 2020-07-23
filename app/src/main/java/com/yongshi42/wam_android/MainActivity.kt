package com.yongshi42.wam_android

import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.yongshi42.wam_android.api_stuff.APIKindaStuff
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * receive accountjson from previous activity and store in a viewmodel
         */
        var accountjson: JsonObject = Gson().fromJson(intent.getStringExtra("accountjson"), JsonObject::class.java)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mainViewModel.accountjson.value = accountjson
        Log.d("ethnfc debug", "Mainactivity retrieves accountjson in mainviewmodel ${mainViewModel} as ${mainViewModel.accountjson.value.toString()}")

        APIKindaStuff
            .service
            .getCharacters(accountjson["pubkey"].toString())
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {
                    Log.d("ethnfc debug", "--- :: GET Throwable EXCEPTION:: ${t.message}")
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        var responsestring = response.body()?.string().toString()
                        Log.d("ethnfc debug", "GET characters: $responsestring")
                        mainViewModel.charactersjson.value = Gson().fromJson(responsestring, JsonObject::class.java)
                        mainViewModel.challengerjson.value = mainViewModel.charactersjson.value?.get("1")?.asJsonObject
                    }
                }
            })

        Log.d("ethnfc debug", "Mainactivity sets charactersjson in mainviewmodel ${mainViewModel} as ${mainViewModel.charactersjson.value.toString()}")

        /**
         * set some menu / navigation stuff
         */
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_account, R.id.nav_challenger, R.id.nav_opponent, R.id.nav_history, R.id.nav_duel
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        /**
         * Add check to ping server every time app is resumed
         */
    }
}