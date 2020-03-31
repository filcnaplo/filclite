package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.data.Student

class NotesUI {
    companion object {
        fun generateNotes(ctx: Context, cachedStudent: Student, noteHolder: LinearLayout?, detailLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit) {
            for (note in cachedStudent.notes) {
                val noteButton = Button(ctx)
                noteButton.text = "${note.type} | ${note.title} | ${note.teacher}"
                noteButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                noteButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                noteButton.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                noteButton.setOnClickListener {
                    hideDetails()
                    val noteDetailsTextView = TextView(ctx)
                    noteDetailsTextView.text = "${note.title} (${note.type}) \n" +
                            "${note.content} \n" +
                            "${note.teacher} (${note.date})"
                    noteDetailsTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    detailLL.addView(noteDetailsTextView)
                    showDetails()
                }
                noteHolder?.addView(noteButton)
            }
        }
    }
}