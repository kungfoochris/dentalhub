package com.abhiyantrik.dentalhub.interfaces

import android.content.Context
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.entities.Activity
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.models.District
import com.abhiyantrik.dentalhub.models.Geography
import com.abhiyantrik.dentalhub.models.LoginResponse
import com.abhiyantrik.dentalhub.models.Profile
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import com.abhiyantrik.dentalhub.models.Activity as ActivityModel
import com.abhiyantrik.dentalhub.models.Encounter as EncounterModel
import com.abhiyantrik.dentalhub.models.History as HistoryModel
import com.abhiyantrik.dentalhub.models.Patient as PatientModel
import com.abhiyantrik.dentalhub.models.Referral as ReferralModel
import com.abhiyantrik.dentalhub.models.Screening as ScreeningModel
import com.abhiyantrik.dentalhub.models.Treatment as TreatmentModel

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
        @Field("geography_id") geography_id: String,
        @Field("author") author: String,
        @Field("updated_by") updated_by: String,
        @Field("created_at") createdAt: String?,
        @Field("updated_at") updatedAt: String?
    ): Call<PatientModel>

    @GET("patients")
    fun getPatients(@Header("Authorization") token: String): Call<List<PatientModel>>

    @GET("patients/{patientId}/encounters")
    fun getEncounter(@Header("Authorization") token: String, @Path("patientId") patientId: String): Call<List<EncounterModel>>

    @FormUrlEncoded
    @POST("activities")
    fun addActivity(
        @Header("Authorization") token: String,
        @Field("activity_id") area: String,
        @Field("area") name: String
    ): Call<ActivityModel>

    @FormUrlEncoded
    @POST("patients/{user}/encounters")
    fun addEncounter(
        @Header("Authorization") token: String,
        @Path("user") user: String,
        @Field("id") id: String,
        @Field("geography_id") geographyId: String,
        @Field("activityarea_id") activityareaId: String,
        @Field("encounter_type") encounterType: String,
        @Field("other_detail") otherDetail: String
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
        @Field("no_underlying_medical") underlying_medical: Boolean,
        @Field("not_taking_medication") no_taking_medication: Boolean,
        @Field("medication") medication: String,
        @Field("no_allergies") no_allergic: Boolean,
        @Field("allergies") allergic: String
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
        @Field("reversible_pulpitis") reversible_pulpitis: Boolean,
        @Field("art") need_art_filling: Boolean,
        @Field("need_sealant") need_sealant: Boolean,
        @Field("need_sdf") need_sdf: Boolean,
        @Field("extraction") need_extraction: Boolean,
        @Field("active_infection") active_infection: Boolean,
        @Field("high_blood_pressure") high_blood_pressure: Boolean,
        @Field("low_blood_pressure") low_blood_pressure: Boolean,
        @Field("thyroid") thyroid: Boolean
    ): Call<ScreeningModel>

    @FormUrlEncoded
    @POST("encounter/{remoteId}/treatment")
    fun addTreatment(
        @Header("Authorization") token: String,
        @Path("remoteId") encounterRemoteId: String,
        @Field("id") id: Long,

        @Field("tooth18") tooth18: String,
        @Field("tooth17") tooth17: String,
        @Field("tooth16") tooth16: String,
        @Field("tooth15") tooth15: String,
        @Field("tooth14") tooth14: String,
        @Field("tooth13") tooth13: String,
        @Field("tooth12") tooth12: String,
        @Field("tooth11") tooth11: String,

        @Field("tooth21") tooth21: String,
        @Field("tooth22") tooth22: String,
        @Field("tooth23") tooth23: String,
        @Field("tooth24") tooth24: String,
        @Field("tooth25") tooth25: String,
        @Field("tooth26") tooth26: String,
        @Field("tooth27") tooth27: String,
        @Field("tooth28") tooth28: String,

        @Field("tooth48") tooth48: String,
        @Field("tooth47") tooth47: String,
        @Field("tooth46") tooth46: String,
        @Field("tooth45") tooth45: String,
        @Field("tooth44") tooth44: String,
        @Field("tooth43") tooth43: String,
        @Field("tooth42") tooth42: String,
        @Field("tooth41") tooth41: String,

        @Field("tooth31") tooth31: String,
        @Field("tooth32") tooth32: String,
        @Field("tooth33") tooth33: String,
        @Field("tooth34") tooth34: String,
        @Field("tooth35") tooth35: String,
        @Field("tooth36") tooth36: String,
        @Field("tooth37") tooth37: String,
        @Field("tooth38") tooth38: String,

        @Field("tooth55") tooth55: String,
        @Field("tooth54") tooth54: String,
        @Field("tooth53") tooth53: String,
        @Field("tooth52") tooth52: String,
        @Field("tooth51") tooth51: String,

        @Field("tooth61") tooth61: String,
        @Field("tooth62") tooth62: String,
        @Field("tooth63") tooth63: String,
        @Field("tooth64") tooth64: String,
        @Field("tooth65") tooth65: String,

        @Field("tooth85") tooth85: String,
        @Field("tooth84") tooth84: String,
        @Field("tooth83") tooth83: String,
        @Field("tooth82") tooth82: String,
        @Field("tooth81") tooth81: String,

        @Field("tooth71") tooth71: String,
        @Field("tooth72") tooth72: String,
        @Field("tooth73") tooth73: String,
        @Field("tooth74") tooth74: String,
        @Field("tooth75") tooth75: String,

        @Field("whole_mouth") sdf_whole_mouth: Boolean,
        @Field("fluoride_varnish") fv_applied: Boolean,
        @Field("treatment_complete") treatment_plan_complete: Boolean,
        @Field("note") notes: String
    ): Call<TreatmentModel>

    @FormUrlEncoded
    @POST("encounter/{remoteId}/refer")
    fun addReferral(
        @Header("Authorization") token: String,
        @Path("remoteId") encounterRemoteId: String,
        @Field("id") id: Long = 0,
        @Field("no_referal") no_referral: Boolean,
        @Field("health_post") health_post: Boolean,
        @Field("hygienist") hygienist: Boolean,
        @Field("dentist") dentist: Boolean,
        @Field("physician") general_physician: Boolean,
        @Field("other") other_details: String,
        @Field("date") date: String,
        @Field("time") time: String
    ): Call<ReferralModel>

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

    @GET("profile")
    fun fetchProfile(@Header("Authorization") token: String): Call<Profile>

    // to get all activity list
    @GET("events")
    fun listActivityEvents(@Header("Authorization") token: String): Call<List<ActivityModel>>

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