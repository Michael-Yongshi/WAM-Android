package com.yongshi42.wam_android.api_stuff

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


/**
 *
 * Created by mohammed on 22/11/17.
 */

class APIKindaStuff {

    interface APIService {
        /**
         * All the get api's are the ones that don't change the blockchain
         * i.e. hashing of a pin and return the hash
         */
        @GET("/get_password_hash/{pin}")
        fun getHashPin(
            @Path("pin") pin: String
        ): Call<ResponseBody>

        @POST("/get_account")
        fun getAccount(@Body body: JsonObject): Call<ResponseBody>

        @POST("/get_characters")
        fun getCharacters(@Body body: String): Call<ResponseBody>

        @POST("/get_character_details")
        fun getCharacterDetails(@Body body: JsonObject): Call<ResponseBody>

        @POST("/get_history")
        fun getHistory(@Body body: JsonObject): Call<ResponseBody>

        @POST("/get_public_key")
        fun getPublicKey(@Body body: JsonObject): Call<ResponseBody>

        @Headers("Content-type: application/json")
        @POST("/api/post_some_data")
        fun getVectors(@Body body: JsonObject): Call<ResponseBody>
    }

    companion object {
        private val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.178.235:5000")
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()

        var service = retrofit.create(
            APIService::class.java)
    }
}