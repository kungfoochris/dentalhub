package com.example.dentalhub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.perf.metrics.AddTrace

class SearchPatientActivity : AppCompatActivity() {

    @AddTrace(name = "onCreateSearchPatientActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_patient)
    }
}
