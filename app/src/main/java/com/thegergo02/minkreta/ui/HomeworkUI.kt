package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.data.Homework

class HomeworkUI {
    companion object {
        fun generateHomework(ctx: Context, homeworkList: List<Homework>, noteHolder: LinearLayout?, detailLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit) {
            for (homework in homeworkList) {
                val noteButton = Button(ctx)
                noteButton.text = ""
                noteButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimaryDark))
                noteButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                noteButton.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                noteButton.setOnClickListener {
                    hideDetails()
                    val noteDetailsTextView = TextView(ctx)
                    noteDetailsTextView.text = ""
                    noteDetailsTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    detailLL.addView(noteDetailsTextView)
                    showDetails()
                }
                noteHolder?.addView(noteButton)
            }
        }
    }
}