package com.abhiyantrik.dentalhub.ui.synchronization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhiyantrik.dentalhub.R
import com.abhiyantrik.dentalhub.adapters.SyncronizationAdapter
import com.abhiyantrik.dentalhub.models.Sync
import com.abhiyantrik.dentalhub.utils.RecyclerViewItemSeparator
import kotlinx.android.synthetic.main.activity_synchronization.*

class SynchronizationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synchronization)

        val sync1 = Sync("MIlan", "Ghimire", "1231", true)
        val sync2 = Sync("Ghana", "Chimire", "1231", false)
        val sync3 = Sync("Prabin", "Mirmire", "1231", false)
        val sync4 = Sync("paras", "Ghimire", "1231", true)

        val synList = mutableListOf<Sync>(sync1, sync2, sync3, sync4)
        
        val adapter = SyncronizationAdapter(synList)
        rvSync.adapter = adapter
        rvSync.layoutManager = LinearLayoutManager(this)
        val divider = RecyclerViewItemSeparator(10)
        rvSync.addItemDecoration(divider)
    }
}
