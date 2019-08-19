package com.example.dentalhub.interfaces

import android.content.Context
import com.example.dentalhub.R
import com.example.dentalhub.entities.Activity
import com.example.dentalhub.models.District
import com.example.dentalhub.entities.Encounter
import com.example.dentalhub.models.Encounter as EncounterModel
import com.example.dentalhub.models.Geography
import com.example.dentalhub.entities.Patient
import com.example.dentalhub.models.LoginResponse
import com.example.dentalhub.models.Patient as PatientModel
import com.example.dentalhub.models.Activity as ActivityModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface DjangoInterface {

    @FormUrlEncoded
    @POST("token/obtain")
    fun login(@Field("username") username: String, @Field("password") password: String): Call<LoginResponse>

    @FormUrlEncoded
    @POST("patients")
    fun addPatient(
        @Header("Authorization") token: String,
        @Field("id") id: Long,
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("gender") gender: String,
        @Field("phone") phone: String,
        @Field("middle_name") middleName: String?,
        @Field("dob") dob: String,
        @Field("education") education: String,
        @Field("ward_id") ward: Int,
        @Field("municipality_id") municipality: Int,
        @Field("district_id") district: Int,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("activityarea_id") activity_area_id: String,
        @Field("geography_id") geography_id: String
    ): Call<PatientModel>

    @FormUrlEncoded
    @POST("activities")
    fun addActivity(
        @Header("Authorization") token: String,
        @Field("area") area: String,
        @Field("name") name: String
    ): Call<ActivityModel>

    @FormUrlEncoded
    @POST("patient/{user}/encounters")
    fun addEncounter(
        @Header("Authorization") token: String,
        @Path("user") user: Int,
        @Field("encounter_type") encounterType: String
    ): Call<EncounterModel>

    @GET("patients")
    fun searchPatient(@Query("s") s: String): Call<List<Patient>>

    @GET("patients")
    fun listPatients(@Header("Authorization") token: String): Call<List<PatientModel>>

    @GET("addresses")
    fun listAddresses(): Call<List<District>>

    @GET("geography")
    fun listGeographies(@Header("Authorization") token: String): Call<List<Geography>>

    @GET("activities")
    fun listActivities(): Call<List<Activity>>

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