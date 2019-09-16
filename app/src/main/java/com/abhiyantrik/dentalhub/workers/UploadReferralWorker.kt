package com.abhiyantrik.dentalhub.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.Encounter
import com.abhiyantrik.dentalhub.entities.Encounter_
import com.abhiyantrik.dentalhub.entities.Referral
import com.abhiyantrik.dentalhub.models.Referral as ReferralModel
import com.abhiyantrik.dentalhub.entities.Referral_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import io.objectbox.Box
import java.lang.Exception

class UploadReferralWorker (context: Context, params: WorkerParameters): Worker(context, params) {

    private lateinit var referralBox: Box<Referral>
    private lateinit var encountersBox: Box<Encounter>

    override fun doWork(): Result {
        return try {
            referralBox = ObjectBox.boxStore.boxFor(Referral::class.java)
            encountersBox = ObjectBox.boxStore.boxFor(Encounter::class.java)
            val encounterId = inputData.getLong("ENCOUNTER_ID", 0)

            val tempReferral = referralBox.query().equal(Referral_.encounterId, encounterId).build().findFirst()!!
            val dbReferralEntity = encountersBox.query().equal(Encounter_.id, encounterId).build().findFirst()
            saveReferralToServer(dbReferralEntity!!.remote_id, tempReferral)
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }

    private fun saveReferralToServer(encounterId: String, referral: Referral) {
        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        val panelService = DjangoInterface.create(applicationContext)
        val call = panelService.addReferral(
            "JWT $token",
            encounterId,
            referral.id,
            referral.no_referral,
            referral.health_post,
            referral.hygienist,
            referral.dentist,
            referral.general_physician,
            referral.other_details,
            "",
        ""
        )
        val tempReferral = call.execute().body() as ReferralModel
        val dbReferralEntity = referralBox.query().equal(Referral_.encounterId, encounterId).build().findFirst()!!
        dbReferralEntity.remote_id = tempReferral.id
        referralBox.put(dbReferralEntity)

    }
}