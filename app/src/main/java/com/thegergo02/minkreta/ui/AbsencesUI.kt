package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.data.Student

class AbsencesUI {
    companion object {
        private fun getColorFromJustification(ctx: Context, justificationState: String?): Int {
            return when (justificationState) {
                "Justified" -> ContextCompat.getColor(ctx, R.color.colorAbsJustified)
                else -> ContextCompat.getColor(ctx, R.color.colorAbsUnjustified)
            }
        }

        fun generateAbsences(ctx: Context, cachedStudent: Student, absenceHolder: LinearLayout?, detailLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit) {
            if (cachedStudent.absences != null) {
                for (abs in cachedStudent.absences) {
                    val absenceButton = Button(ctx)
                    absenceButton.text =
                        "${abs.justificationStateName} | ${abs.subject} | ${abs.lessonStartTime} | ${abs.teacher}"
                    absenceButton.setBackgroundColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.colorPrimaryDark
                        )
                    )
                    absenceButton.setTextColor(
                        getColorFromJustification(
                            ctx,
                            abs.justificationState
                        )
                    )
                    absenceButton.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                    absenceButton.setOnClickListener {
                        hideDetails()
                        val absDetailsTextView = TextView(ctx)
                        absDetailsTextView.text = "Type: ${abs.type} \n" +
                                "Type's name: ${abs.typeName} \n" +
                                "Mode: ${abs.mode} \n" +
                                "Mode's name: ${abs.modeName} \n" +
                                "Subject: ${abs.subject} \n" +
                                "Subject category: ${abs.subjectCategory} \n" +
                                "Subject category's name: ${abs.subjectCategoryName} \n" +
                                "Delay in minutes: ${abs.delayTime} \n" +
                                "Teacher: ${abs.teacher} \n" +
                                "Lesson's start time: ${abs.lessonStartTime} \n" +
                                "Number of lessons: ${abs.numberOfLessons} \n" +
                                "Creating time: ${abs.creatingTime} \n" +
                                "Justification state: ${abs.justificationState} \n" +
                                "Justification state's name: ${abs.justificationStateName} \n" +
                                "Justification type: ${abs.justificationType} \n" +
                                "Justification type's name: ${abs.justificationTypeName} \n" +
                                "Seen by tutelary (UTC): ${abs.seenByTutelaryUtc} \n"
                        absDetailsTextView.setTextColor(
                            ContextCompat.getColor(
                                ctx,
                                R.color.colorText
                            )
                        )
                        detailLL.addView(absDetailsTextView)
                        showDetails()
                    }
                    absenceHolder?.addView(absenceButton)
                }
            }
        }
    }
}