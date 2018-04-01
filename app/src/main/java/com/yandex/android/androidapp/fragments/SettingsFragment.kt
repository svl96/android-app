package com.yandex.android.androidapp.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yandex.android.androidapp.ContainerUI
import com.yandex.android.androidapp.R

class SettingsFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() : SettingsFragment {
            return SettingsFragment()
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

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        containerUi?.setActivityTitle(R.string.settings_title)
        return inflater?.inflate(R.layout.fragment_settings, container, false)!!
    }
}