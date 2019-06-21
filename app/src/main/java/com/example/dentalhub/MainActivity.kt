package com.example.dentalhub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var mainLayout: LinearLayout
    private lateinit var btnAddPatient: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()


    }

    private fun setupUI() {
        btnAddPatient = findViewById(R.id.btnAddPatient)
        btnAddPatient.setOnClickListener {
            startActivity(Intent(this, AddPatientActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.search -> {
                Log.d("PARAS","do the search stuff")
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
