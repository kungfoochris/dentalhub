package com.abhiyantrik.dentalhub.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.ObjectBox
import com.abhiyantrik.dentalhub.entities.User
import com.abhiyantrik.dentalhub.entities.User_
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import timber.log.Timber
import com.abhiyantrik.dentalhub.models.User as UserModel

class DownloadUsersWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private lateinit var usersBox: Box<User>
    private val ctx: Context = context

    override fun doWork(): Result {
        return try {
            usersBox = ObjectBox.boxStore.boxFor(User::class.java)
            downloadUsers()
            return Result.success()
        }catch (e: Exception){
            Timber.d("Exception %s", e.printStackTrace().toString())
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure()
        }
    }

    @AddTrace(name = "downloadUsersFromDownloadUsersWorker", enabled = true /* optional */)
    private fun downloadUsers() {

        val token = DentalApp.readFromPreference(applicationContext, Constants.PREF_AUTH_TOKEN, "")
        if (token.isBlank()) return

        val panelService = DjangoInterface.create(applicationContext)
        val call = panelService.listUsers("JWT $token")

        val response = call.execute()
        if (response.isSuccessful) {
            when (response.code()) {
                200 -> {
                    val allUsers = response.body() as List<UserModel>
                    Timber.d("DownloadUserWorkers: %s", response.body().toString())
                    for(user in allUsers){
                        Timber.d("DownloadUserWorkers: %s %s %s", user.first_name ,user.middle_name, user.last_name)
                        val existingUserCount = usersBox.query().equal(
                            User_.remote_id,
                            user.id
                        ).build().count()
                        if(existingUserCount<1){
                            val userEntity = User()
                            Timber.d("DownloadUserWorkers: %s %s %s", user.first_name, user.middle_name, user.last_name)
                            userEntity.remote_id = user.id
                            userEntity.first_name = user.first_name
                            userEntity.middle_name = user.middle_name
                            userEntity.last_name = user.last_name
                            usersBox.put(userEntity)
                        }else{
                            Timber.d("DUser: %s","User already download")
                        }
                    }
                }
                else -> {
                    Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " listUsers() HTTP Status code "+response.code())
                }
            }
        } else {
            Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " listUsers() Failed to download users.")
            Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " listUsers() HTTP Status code "+response.code())
            Timber.d(DentalApp.readFromPreference(ctx, Constants.PREF_AUTH_EMAIL,"")+ " listUsers() "+response.message())
            Timber.d("DownloadUsersWorker: %s", response.message())
        }
    }
}
