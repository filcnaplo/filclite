package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.data.timetable.SchoolDay

class TimetableUI {
    companion object {
        private fun generateSchoolClasses(ctx: Context, classes: List<SchoolClass>, detailLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit): LinearLayout {
            val timetableClassLL = LinearLayout(ctx)
            timetableClassLL.orientation = LinearLayout.VERTICAL
            for (schoolClass in classes) {
                val classButton = Button(ctx)
                classButton.text = "${schoolClass.count} | ${schoolClass.subject} | ${schoolClass.classRoom} | ${schoolClass.teacher}"
                classButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimaryDark))
                classButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                classButton.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                classButton.setOnClickListener {
                    hideDetails()
                    val classDetailsTextView = TextView(ctx)
                    val deputy = if (schoolClass.deputyTeacher != "") {
                        "(Deputy: ${schoolClass.deputyTeacher})"
                    } else {
                        ""
                    }
                    classDetailsTextView.text = "${schoolClass.subject} \n" +
                            "${schoolClass.startTime.toFormattedString(KretaDate.KretaDateFormat.TIME)}-${schoolClass.endTime.toFormattedString(KretaDate.KretaDateFormat.TIME)} \n" +
                            "${schoolClass.classRoom} \n" +
                            "${schoolClass.teacher} $deputy"
                    classDetailsTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    detailLL.addView(classDetailsTextView)
                    showDetails()
                }
                timetableClassLL.addView(classButton)
            }
            return timetableClassLL
        }

        fun generateTimetable(ctx: Context, currentTimetable: Map<SchoolDay, List<SchoolClass>>, timetableHolder: LinearLayout?, detailLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit, controller: MainController) {
            timetableHolder?.removeAllViews()
            for (day in currentTimetable) {
                val dayButton = Button(ctx)
                dayButton.text = day.key.toString()
                dayButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimaryDark))
                val textColor = if (KretaDate().fromSchoolDay(day.key).isToday()) {
                    ContextCompat.getColor(ctx, R.color.colorText)
                } else {
                    ContextCompat.getColor(ctx, R.color.colorUnavailable)
                }
                dayButton.setTextColor(textColor)
                dayButton.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                dayButton.setOnClickListener {
                    val timetableClassLL = generateSchoolClasses(ctx, day.value, detailLL, showDetails, hideDetails)
                    val timetableBackButton = Button(ctx)
                    timetableBackButton.text = "BACK"
                    timetableBackButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimaryDark))
                    timetableBackButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    timetableBackButton.setOnClickListener {
                        generateTimetable(ctx, currentTimetable, timetableHolder, detailLL, showDetails, hideDetails, controller)
                    }
                    timetableHolder?.removeAllViews()
                    timetableHolder?.addView(timetableBackButton)
                    timetableHolder?.addView(timetableClassLL)
                }
                timetableHolder?.addView(dayButton)
            }
        }
    }
}