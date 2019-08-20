package com.abhiyantrik.dentalhub.interfaces

import android.content.Context
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.Activity
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.models.District
import com.abhiyantrik.dentalhub.models.Geography
import com.abhiyantrik.dentalhub.models.LoginResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import com.abhiyantrik.dentalhub.models.Activity as ActivityModel
import com.abhiyantrik.dentalhub.models.Encounter as EncounterModel
import com.abhiyantrik.dentalhub.models.Patient as PatientModel
import com.abhiyantrik.dentalhub.models.History as HistoryModel
import com.abhiyantrik.dentalhub.models.Screening as ScreeningModel

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
    @POST("patients/{user}/encounters")
    fun addEncounter(
        @Header("Authorization") token: String,
        @Path("user") user: String,
        @Field("geography_id") geographyId: String,
        @Field("activityarea_id") activityareaId: String,
        @Field("encounter_type") encounterType: String
    ): Call<EncounterModel>

    @FormUrlEncoded
    @POST("encounter/{remoteId}/history")
    fun addHistory(
        @Header("Authorization") token: String,
        @Path("remoteId") remoteId: String,
        @Field("id") id: Long,
        @Field("bleeding") bleeding: Boolean,
        @Field("diabetes") diabetes: Boolean,
        @Field("liver") liver: Boolean,
        @Field("fever") fever: Boolean,
        @Field("seizures") seizures: Boolean,
        @Field("hepatitis") hepatitis: Boolean,
        @Field("hiv") hiv: Boolean,
        @Field("other") other: String,
        @Field("underlyingmedical") underlying_medical: Boolean,
        @Field("no_medication") no_taking_medication: Boolean,
        @Field("medication") medication: String,
        @Field("noallergic") no_allergic: Boolean,
        @Field("allergic") allergic: String
    ): Call<HistoryModel>

    @FormUrlEncoded
    @POST("encounter/{remoteId}/screening")
    fun addScreening(
        @Header("Authorization") token: String,
        @Path("remoteId") encounterRemoteId: String,
        @Field("caries_risk") carries_risk: String,
        @Field("primary_teeth") decayed_pimary_teeth: Int,
        @Field("permanent_teeth") decayed_permanent_teeth: Int,
        @Field("anterior_teeth") cavity_permanent_anterior: Boolean,
        @Field("postiror_teeth") cavity_permanent_tooth: Boolean,
        @Field("reversible_pulpitis") active_infection: Boolean,
        @Field("art") need_art_filling: Boolean,
        @Field("need_sealant") need_sealant: Boolean,
        @Field("need_sdf") need_sdf: Boolean,
        @Field("extraction") need_extraction: Boolean
    ): Call<ScreeningModel>

    @FormUrlEncoded
    @POST("encounter/{remoteId}/treatment")
    fun addTreatment(
        @Header("Authorization") token: String,
        @Path("remoteId") encounterRemoteId: String
    )

    @FormUrlEncoded
    @POST("encounter/{remoteId}/refer")
    fun addReferral(
        @Header("Authorization") token: String,
        @Path("remoteId") encounterRemoteId: String
    )

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