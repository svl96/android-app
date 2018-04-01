package com.yandex.android.androidapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yandex.android.androidapp.R


class AboutAppFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() : AboutAppFragment {
            return AboutAppFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater?.inflate(R.layout.fragment_about_app, container, false)!!
    }
}