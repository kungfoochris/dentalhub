package com.example.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dentalhub.interfaces.DjangoInterface
import com.example.dentalhub.models.LoginResponse
import com.example.dentalhub.utils.EmailValidator
import com.google.firebase.perf.metrics.AddTrace
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvErrorMessage: TextView
    private lateinit var loading: ProgressBar
    private lateinit var btnLogin: Button
    private lateinit var context: Context

    private val TAG = "LoginActivity"

    @AddTrace(name = "onCreateTrace", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        context = this

        setupUI()
    }

    @AddTrace(name = "setupUI", enabled = true /* optional */)
    private fun setupUI() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        loading = findViewById(R.id.loading)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)

        btnLogin.setOnClickListener {
            if (formIsValid()) {
                processLogin()
            }
        }
    }

    @AddTrace(name = "processLogin", enabled = true /* optional */)
    private fun processLogin() {
        Log.d(TAG, "processLogin()")
        loading.visibility = View.VISIBLE
        tvErrorMessage.visibility = View.GONE
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val panelService = DjangoInterface.create(this)
        val call = panelService.login(email, password)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d(TAG, "onResponse()")
                Log.d("Resp", response.toString())
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            val loginResponse = response.body() as LoginResponse
                            DentalApp.saveToPreference(context, Constants.PREF_AUTH_TOKEN, loginResponse.token)
                            DentalApp.saveToPreference(context, Constants.PREF_AUTH_EMAIL, email)
                            DentalApp.saveToPreference(context, Constants.PREF_AUTH_PASSWORD, password)
                            startActivity(Intent(context, SelectorActivity::class.java))
                        }
                        400 -> {
                            tvErrorMessage.text = getString(R.string.error_http_400)
                            tvErrorMessage.visibility = View.VISIBLE
                        }
                        404 -> {
                            tvErrorMessage.text = getString(R.string.error_http_404)
                            tvErrorMessage.visibility = View.VISIBLE
                        }
                        else -> {
                            tvErrorMessage.text = getString(R.string.error_http_500)
                            tvErrorMessage.visibility = View.VISIBLE
                        }
                    }
                    loading.visibility = View.GONE
                } else {
                    tvErrorMessage.text = response.message()
                    tvErrorMessage.visibility = View.VISIBLE
                    loading.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                tvErrorMessage.text = t.message.toString()
                tvErrorMessage.visibility = View.VISIBLE
                loading.visibility = View.GONE
            }

        })

    }

    @AddTrace(name = "formIsValid", enabled = true /* optional */)
    private fun formIsValid(): Boolean {
        tvErrorMessage.visibility = View.GONE
        var status = false
        if (etEmail.text.isBlank()) {
            status = false
            tvErrorMessage.text = getString(R.string.email_is_required)
            tvErrorMessage.visibility = View.VISIBLE
        } else if (!EmailValidator.isEmailValid(etEmail.text.toString())) {
            status = false
            tvErrorMessage.text = getString(R.string.invalid_email)
            tvErrorMessage.visibility = View.VISIBLE
        } else if (etPassword.text.isBlank()) {
            status = false
            tvErrorMessage.text = getString(R.string.password_is_required)
            tvErrorMessage.visibility = View.VISIBLE
        } else {
            status = true
        }
        return status
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}