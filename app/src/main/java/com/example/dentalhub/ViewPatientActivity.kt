package com.example.dentalhub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ViewPatientActivity: AppCompatActivity(){
    private lateinit var btnAddNewEncounter: Button
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_patient)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        context = this
        initUI()
    }

    private fun initUI() {
        btnAddNewEncounter = findViewById(R.id.btnAddNewEncounter)
        btnAddNewEncounter.setOnClickListener {
            startActivity(Intent(context, AddEncounterActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view_patient,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.editPatient -> {
                val addPatientIntent = Intent(this, AddPatientActivity::class.java)
                startActivity(addPatientIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}