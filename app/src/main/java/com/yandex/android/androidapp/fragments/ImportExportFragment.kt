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
import com.yandex.android.androidapp.NotesContainerUI
import com.yandex.android.androidapp.R
import com.yandex.android.androidapp.services.ImportExportService


class ImportExportFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() : ImportExportFragment {
            return ImportExportFragment()
        }
    }

    private var containerUi: ContainerUI? = null
    private var notesContainer : NotesContainerUI? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is ContainerUI)
            containerUi = context
        else
            throw IllegalStateException("Context should implement ContainerUI")

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        containerUi?.setActivityTitle(R.string.import_export_title)
        notesContainer = containerUi?.getNotesContainer()

        val rootView = inflater?.inflate(R.layout.fragment_import_export, container, false)!!

        val exportButton = rootView.findViewById<Button>(R.id.export_button)
        val importButton = rootView.findViewById<Button>(R.id.import_button)

        importButton.setOnClickListener { importNotes() }
        exportButton.setOnClickListener { exportNotes() }

        return rootView
    }

    private fun exportNotes() {
        val intentService = Intent(context, ImportExportService::class.java)
        intentService.putExtra(ImportExportService.EXTRA_IMPORT_EXPORT_MODE,
                ImportExportService.EXPORT_MODE)

        context.startService(intentService)
    }

    private fun importNotes() {
        val intentService = Intent(context, ImportExportService::class.java)
        intentService.putExtra(ImportExportService.EXTRA_IMPORT_EXPORT_MODE,
                ImportExportService.IMPORT_MODE)

        context.startService(intentService)
    }


}