package com.abhiyantrik.dentalhub.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.abhiyantrik.dentalhub.Constants
import com.abhiyantrik.dentalhub.DentalApp
import com.abhiyantrik.dentalhub.R

class TickerFragment : Fragment() {
    private lateinit var textView: TextView
    private lateinit var ctx: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ticker, container, false)
        textView = view.findViewById<TextView>(R.id.textView)
        when (DentalApp.activity_name) {
            "Health Post" -> {
                view.setBackgroundColor(ContextCompat.getColor(ctx, R.color.blue_500))
            }
            "School Seminar" -> {
                view.setBackgroundColor(ContextCompat.getColor(ctx, R.color.red_500))
            }
            "Community Outreach" -> {
                view.setBackgroundColor(ContextCompat.getColor(ctx, R.color.green_500))
            }
            "Training" -> {
                view.setBackgroundColor(ContextCompat.getColor(ctx, R.color.black))
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val tickerText = DentalApp.readFromPreference(
                activity as Context,
        Constants.PREF_PROFILE_FULL_NAME,
        ""
        ) + " | " + DentalApp.ward_name + " | " + DentalApp.activity_name + " | " + DentalApp.activity_area_name
        textView.text = tickerText

    }
}
