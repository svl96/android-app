package com.yandex.android.androidapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.yandex.android.androidapp.ContainerUI
import com.yandex.android.androidapp.R
import com.yandex.android.androidapp.services.ThousandsNotesService


class AboutAppFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() : AboutAppFragment {
            return AboutAppFragment()
        }
    }

    private var containerUi: ContainerUI? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ContainerUI)
            containerUi = context
        else
            throw IllegalStateException("Context should implement ContainerUI")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        containerUi?.setActivityTitle(R.string.about_app_title)
        val rootView = inflater?.inflate(R.layout.fragment_about_app, container, false)!!

        val specialButton = rootView.findViewById<Button>(R.id.add_1000_notes)
        specialButton.setOnClickListener { addNotes() }

        return rootView
    }

    private fun addNotes() {
        val newIntentService = Intent(context, ThousandsNotesService::class.java)
        context.startService(newIntentService)
    }
}