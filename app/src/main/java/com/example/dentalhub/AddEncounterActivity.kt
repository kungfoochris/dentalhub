package com.example.dentalhub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.dentalhub.adapters.FormPageAdapter
import com.google.android.material.tabs.TabLayout

class AddEncounterActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_encounter)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUI()
    }

    private fun initUI() {
        pager = findViewById(R.id.pager)
        tabLayout = findViewById(R.id.tabLayout)

        val fragmentAdapter = FormPageAdapter(supportFragmentManager)
        pager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(pager)

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
