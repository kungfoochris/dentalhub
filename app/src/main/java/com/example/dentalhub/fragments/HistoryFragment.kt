package com.example.dentalhub.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.dentalhub.R


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HistoryFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HistoryFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }



}
