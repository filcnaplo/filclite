package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.kreta.data.Student
import com.thegergo02.minkreta.kreta.data.sub.Note

class NotesUI {
    companion object {
        fun generateNotes(ctx: Context, noteList: List<Note>, noteHolder: LinearLayout?, detailsLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit) {
            for (note in noteList) {
                val text = note.toString()
                val noteOnClickListener = {
                    _: View ->
                    val noteDetailsTextView = TextView(ctx)
                    noteDetailsTextView.text = note.toDetailedString()
                    noteDetailsTextView.setTextColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.colorText
                        )
                    )
                    listOf(noteDetailsTextView)
                }
                val noteButton = UIHelper.generateButton(ctx, text, noteOnClickListener, showDetails, hideDetails, detailsLL)
                noteHolder?.addView(noteButton)
            }
        }
    }
}