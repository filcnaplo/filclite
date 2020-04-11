package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.data.Student

class AbsencesUI {
    companion object {
        private fun getColorFromJustification(ctx: Context, justificationState: String?): Int {
            return when (justificationState) {
                "Justified" -> R.color.colorAbsJustified
                else -> R.color.colorAbsUnjustified
            }
        }

        fun generateAbsences(ctx: Context, cachedStudent: Student, absenceHolder: LinearLayout?, detailsLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit) {
            if (cachedStudent.absences != null) {
                for (abs in cachedStudent.absences) {
                    val text =
                        "${abs.justificationStateName} | ${abs.subject} | ${abs.lessonStartTime?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)} | ${abs.teacher}"
                    val absOnClickListener = {
                            _: View ->
                        val absDetailsTextView = TextView(ctx)
                        absDetailsTextView.text = "${abs.subject} (${abs.teacher})\n" +
                                "${abs.typeName} (${abs.modeName})\n" +
                                "${abs.justificationStateName} (${abs.justificationTypeName})\n" +
                                "${abs.delayTime} seconds late \n" +
                                "${abs.creatingTime?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)} \n"
                        absDetailsTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                        listOf(absDetailsTextView)
                    }
                    val absenceButton =
                        UIHelper.generateButton(ctx, text, absOnClickListener, showDetails, hideDetails, detailsLL, getColorFromJustification(ctx, abs.justificationState))
                    absenceHolder?.addView(absenceButton)
                }
            }
        }
    }
}