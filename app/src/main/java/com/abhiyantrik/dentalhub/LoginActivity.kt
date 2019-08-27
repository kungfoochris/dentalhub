package com.abhiyantrik.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.app.Activity;
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import com.abhiyantrik.dentalhub.entities.Geography
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.LoginResponse
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : Activity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvErrorMessage: TextView
    private lateinit var loading: ProgressBar
    private lateinit var btnLogin: Button
    private lateinit var context: Context
    private lateinit var geographiesBox: Box<Geography>

    private val TAG = "LoginActivity"

    @AddTrace(name = "onCreateTrace", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        context = this
        geographiesBox = ObjectBox.boxStore.boxFor(Geography::class.java)
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
            tvErrorMessage.visibility = View.GONE
            if (formIsValid()) {
                processLogin()
            }
        }
        etPassword.setOnEditorActionListener(object: TextView.OnEditorActionListener{
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if(p1 == EditorInfo.IME_ACTION_GO){
                    tvErrorMessage.visibility = View.GONE
                    if (formIsValid()) {
                        processLogin()
                    }
                    return true
                }
                return false
            }

        })
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
                            DentalApp.saveToPreference(
                                context,
                                Constants.PREF_AUTH_TOKEN,
                                loginResponse.token
                            )
                            DentalApp.saveToPreference(context, Constants.PREF_AUTH_EMAIL, email)
                            DentalApp.saveToPreference(
                                context,
                                Constants.PREF_AUTH_PASSWORD,
                                password
                            )
                            startActivity(Intent(context, SetupActivity::class.java))
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
                    if (response.code() == 400) {
                        tvErrorMessage.text = getString(R.string.username_password_dont_matched)
                        tvErrorMessage.visibility = View.VISIBLE
                    }
                    Log.d("response CODE", response.code().toString())
                    Log.d("response BODY", response.errorBody().toString())
//                    loading.visibility = View.GONE
//                    val gson = Gson()
//                    val errorResponse = gson.fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
//                    tvErrorMessage.text = errorResponse.non_field_errors[0]
//                    tvErrorMessage.visibility = View.VISIBLE

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
            tvErrorMessage.text = getString(R.string.username_is_required)
            tvErrorMessage.visibility = View.VISIBLE
//        } else if (!EmailValidator.isEmailValid(etEmail.text.toString())) {
//            status = false
//            tvErrorMessage.text = getString(R.string.invalid_email)
//            tvErrorMessage.visibility = View.VISIBLE
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