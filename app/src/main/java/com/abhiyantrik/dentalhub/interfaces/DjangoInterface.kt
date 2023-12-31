package com.abhiyantrik.dentalhub.interfaces

import android.content.Context
import com.abhiyantrik.dentalhub.BuildConfig
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.models.*
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
import com.abhiyantrik.dentalhub.models.User as UserModel

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
        @Field("activityarea_id") activityAreaId: String,
        @Field("area") area: String,
        @Field("geography_id") geographyId: Int,
        @Field("recall_date") recallDate: String,
        @Field("recall_time") recallTime : String,
        @Field("recall_geography") recallGeography: Int,
        @Field("author") author: String,
        @Field("updated_by") updatedBy: String,
        @Field("created_at") createdAt: String?,
        @Field("updated_at") updatedAt: String?
    ): Call<PatientModel>

    @FormUrlEncoded
    @PUT("patient/{patientId}")
    fun updatePatient(
        @Header("Authorization") token: String,
        @Path("patientId") patientId: String,
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
        @Field("activityarea_id") activityAreaId: String,
        @Field("geography_id") geographyId: Int,
        @Field("recall_date") recallDate: String,
        @Field("recall_time") recallTime : String,
        @Field("recall_geography") recallGeography: Int,
        @Field("author") author: String,
        @Field("updated_by") updatedBy: String,
        @Field("created_at") createdAt: String?,
        @Field("updated_at") updatedAt: String?
    ): Call<PatientModel>


//    For all patients url is '/patients'
//    For geography restricted users url is '/accesspatients'
    @GET("accesspatients")
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
        @Field("id") id: Int,
        @Field("geography_id") geographyId: Int,
        @Field("activityarea_id") activitareaId: String,
        @Field("area") area: String,
        @Field("encounter_type") encounterType: String,
        @Field("other_problem") otherProblem: String,
        @Field("author") author: String,
        @Field("created_at") createdAt: String,
        @Field("updated_at") updatedAt: String,
        @Field("updated_by") updatedBy: String
    ): Call<EncounterModel>

    @FormUrlEncoded
    @POST("encounter/{encounterId}/history")
    fun addHistory(
        @Header("Authorization") token: String,
        @Path("encounterId") encounterId: String,
        @Field("id") id: Long,
        @Field("blood_disorder") bloodDisorder: Boolean,
        @Field("diabetes") diabetes: Boolean,
        @Field("liver_problem") liverProblem: Boolean,
        @Field("rheumatic_fever") rheumaticFever: Boolean,
        @Field("seizuers_or_epilepsy") seizuersOrEpilepsy: Boolean,
        @Field("hepatitis_b_or_c") hepatitisBOrC: Boolean,
        @Field("hiv") hiv: Boolean,
        @Field("no_allergies") noAllergies: Boolean,
        @Field("allergies") allergies: String,
        @Field("other") other: String,
        @Field("high_blood_pressure") highBloodPressure: Boolean,
        @Field("low_blood_pressure") lowBloodPressure: Boolean,
        @Field("thyroid_disorder") thyroidDisorder: Boolean,
        @Field("medications") medications: String,
        @Field("no_underlying_medical_condition") noUnderlyingMedicalCondition: Boolean,
        @Field("not_taking_any_medications") notTakingAnyMedications: Boolean
    ): Call<HistoryModel>

    @FormUrlEncoded
    @PUT("encounter/{encounterId}/history/update")
    fun updateHistory(
        @Header("Authorization") token: String,
        @Path("encounterId") encounterId: String,
        @Field("id") id: Long,
        @Field("blood_disorder") bloodDisorder: Boolean,
        @Field("diabetes") diabetes: Boolean,
        @Field("liver_problem") liverProblem: Boolean,
        @Field("rheumatic_fever") rheumaticFever: Boolean,
        @Field("seizuers_or_epilepsy") seizuersOrEpilepsy: Boolean,
        @Field("hepatitis_b_or_c") hepatitisBOrC: Boolean,
        @Field("hiv") hiv: Boolean,
        @Field("no_allergies") noAllergies: Boolean,
        @Field("allergies") allergies: String,
        @Field("other") other: String,
        @Field("high_blood_pressure") highBloodPressure: Boolean,
        @Field("low_blood_pressure") lowBloodPressure: Boolean,
        @Field("thyroid_disorder") thyroidDisorder: Boolean,
        @Field("medications") medications: String,
        @Field("no_underlying_medical_condition") noUnderlyingMedicalCondition: Boolean,
        @Field("not_taking_any_medications") notTakingAnyMedications: Boolean
    ): Call<HistoryModel>

    @FormUrlEncoded
    @POST("encounter/{encounterId}/screening")
    fun addScreening(
        @Header("Authorization") token: String,
        @Path("encounterId") encounterId: String,
        @Field("carries_risk") carriesRisk: String,
        @Field("decayed_primary_teeth") decayedPrimaryTeeth: Int,
        @Field("decayed_permanent_teeth") decayedPermanentTeeth: Int,
        @Field("cavity_permanent_posterior_teeth") cavityPermanentPosteriorTeeth: Boolean,
        @Field("cavity_permanent_anterior_teeth") cavityPermanentAnteriorTeeth: Boolean,
        @Field("need_sealant") needSealant: Boolean,
        @Field("reversible_pulpitis") reversiblePulpitis: Boolean,
        @Field("need_art_filling") needArtFilling: Boolean,
        @Field("need_extraction") needExtraction: Boolean,
        @Field("need_sdf") needSDF: Boolean,
        @Field("active_infection") activeInfection: Boolean

    ): Call<ScreeningModel>

    @FormUrlEncoded
    @PUT("encounter/{encounterId}/screening/update")
    fun updateScreening(
        @Header("Authorization") token: String,
        @Path("encounterId") encounterId: String,
        @Field("carries_risk") carriesRisk: String,
        @Field("decayed_primary_teeth") decayedPrimaryTeeth: Int,
        @Field("decayed_permanent_teeth") decayedPermanentTeeth: Int,
        @Field("cavity_permanent_anterior_teeth") cavityPermanentAnteriorTeeth: Boolean,
        @Field("cavity_permanent_posterior_teeth") cavityPermanentPosteriorTeeth: Boolean,
        @Field("reversible_pulpitis") reversiblePulpitis: Boolean,
        @Field("need_art_filling") needArtFilling: Boolean,
        @Field("need_sealant") needSealant: Boolean,
        @Field("need_sdf") needSDF: Boolean,
        @Field("need_extraction") needExtraction: Boolean,
        @Field("active_infection") activeInfection: Boolean
    ): Call<ScreeningModel>

    @FormUrlEncoded
    @POST("encounter/{encounterId}/treatment")
    fun addTreatment(
        @Header("Authorization") token: String,
        @Path("encounterId") encounterId: String,
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

        @Field("sdf_whole_mouth") sdfWholeMouth: Boolean,
        @Field("fv_applied") fvApplied: Boolean,
        @Field("treatment_plan_complete") treatmentPlanComplete: Boolean,
        @Field("notes") notes: String
    ): Call<TreatmentModel>


    @FormUrlEncoded
    @PUT("encounter/{encounterId}/treatment/update")
    fun updateTreatment(
        @Header("Authorization") token: String,
        @Path("encounterId") encounterId: String,
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

        @Field("sdf_whole_mouth") sdfWholeMouth: Boolean,
        @Field("fv_applied") fvApplied: Boolean,
        @Field("treatment_plan_complete") treatmentPlanComplete: Boolean,
        @Field("notes") notes: String
    ): Call<TreatmentModel>

    @FormUrlEncoded
    @POST("encounter/{remoteId}/refer")
    fun addReferral(
        @Header("Authorization") token: String,
        @Path("remoteId") encounterRemoteId: String,
        @Field("id") id: Long = 0,
        @Field("no_referal") noReferral: Boolean,
        @Field("health_post") healthPost: Boolean,
        @Field("hygienist") hygienist: Boolean,
        @Field("dentist") dentist: Boolean,
        @Field("general_physician") generalPhysician: Boolean,
        @Field("other") otherDetails: String
    ): Call<ReferralModel>

    @FormUrlEncoded
    @PUT("encounter/{encounterId}/refer/update")
    fun updateReferral(
        @Header("Authorization") token: String,
        @Path("encounterId") encounterId: String,
        @Field("id") id: Long,
        @Field("no_referal") noReferral: Boolean,
        @Field("health_post") healthPost: Boolean,
        @Field("dentist") dentist: Boolean,
        @Field("general_physician") generalPhysician: Boolean,
        @Field("hygienist") hygienist: Boolean,
        @Field("other_details") otherDetails: String
    ): Call<ReferralModel>

    @GET("patients")
    fun searchPatient(@Query("s") s: String): Call<List<PatientModel>>

    @GET("patients")
    fun listPatients(@Header("Authorization") token: String): Call<List<PatientModel>>

    @GET("users")
    fun listUsers(@Header("Authorization") token:String): Call<List<UserModel>>

    @GET("addresses")
    fun listAddresses(): Call<List<District>>

    @GET("geography")
    fun listGeographies(@Header("Authorization") token: String): Call<List<Geography>>

    @GET("activities")
    fun listActivities(): Call<List<ActivityAreaModel>>

    @GET("profile")
    fun fetchProfile(@Header("Authorization") token: String): Call<Profile>

    // to get all activity list
    @GET("events")
    fun listActivityEvents(@Header("Authorization") token: String): Call<List<ActivityModel>>

    @FormUrlEncoded
    @POST("modifydelete")
    fun modifyEncounterFlag(
        @Header("Authorization")
        token: String,
        @Field("encounter")
        encounterId: String,
        @Field("reason_for_modification")
        reasonForModification: String,
        @Field("flag")
        flag: String
    ): Call<FlagResponse>

    @FormUrlEncoded
    @PUT("flagdead/{flagId}")
    fun changeFlagToModified(
        @Header("Authorization")
        token: String,
        @Path("flagId")
        flagId: Long,
        @Field("modify_status")
        modifyStatus: String
    ): Call<FlagEncounterModifiedSubmit>

    @FormUrlEncoded
    @POST("modifydelete")
    fun deleteEncounterFlag(
        @Header("Authorization")
        token: String,
        @Field("encounter")
        encounterId: String,
        @Field("reason_for_deletion")
        reasonForDeletion: String,
        @Field("other_reason_for_deletion")
        otherReasonForDeletion: String,
        @Field("flag")
        flag: String
    ): Call<FlagResponse>

    @GET("modifydelete")
    fun listFlaggedData(
        @Header("Authorization")
        token: String
    ): Call<List<FlagModifyDelete>>

    companion object Factory {
        fun create(context: Context): DjangoInterface {
            val gson: Gson = GsonBuilder().create()
            val baseURL = context.getString(R.string.prod_api_url)
//            val baseURL = if(BuildConfig.DEBUG){
//                context.getString(R.string.test_api_url);
//            }else{
//                context.getString(R.string.prod_api_url);
//            }
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson)).build()

            return retrofit.create(DjangoInterface::class.java)
        }

    }
}