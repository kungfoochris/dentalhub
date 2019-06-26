package com.example.dentalhub.interfaces

import android.content.Context
import com.example.dentalhub.R
import com.example.dentalhub.models.LoginResponse
import com.example.dentalhub.models.Patient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface DjangoInterface {

    @FormUrlEncoded
    @POST("token/obtain")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("patients")
    fun addPatient(
        @Field("id") id:String,
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("address") address:String
    )

    @GET("patients")
    fun searchPatient(@Query("s") s: String): Call<List<Patient>>

    @GET("patients")
    fun listPatients(@Header("Authorization") token: String) : Call<List<Patient>>

    companion object Factory {
        fun create(context: Context): DjangoInterface {
            val gson: Gson = GsonBuilder().create()
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(context.getString(R.string.api_url))
                .addConverterFactory(GsonConverterFactory.create(gson)).build()
            return retrofit.create(DjangoInterface::class.java)
        }

    }
}