package com.example.dentalhub

import android.app.ListActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.SearchManager
import android.content.Intent
import androidx.media.session.MediaButtonReceiver.handleIntent





class SearchActivity : ListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                doSearch(query)
            }
        }


    }
    private fun doSearch(queryStr: String) {
        // get a Cursor, prepare the ListAdapter
        // and set it
    }
}
