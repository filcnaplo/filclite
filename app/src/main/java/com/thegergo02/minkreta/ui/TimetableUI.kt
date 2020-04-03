package com.thegergo02.minkreta.ui

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.data.Student
import com.thegergo02.minkreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.data.timetable.SchoolDay

class TimetableUI {
    companion object {
        private fun generateSchoolClasses(ctx: Context, classes: List<SchoolClass>): LinearLayout {
            val timetableClassLL = LinearLayout(ctx)
            timetableClassLL.orientation = LinearLayout.VERTICAL
            for (schoolClass in classes) {
                val classButton = Button(ctx)
                classButton.text = "${schoolClass.subject} | ${schoolClass.classRoom} | ${schoolClass.teacher}"
                classButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                classButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                classButton.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                timetableClassLL.addView(classButton)
                if (schoolClass.startTime != null && schoolClass.subject != null) {
                    Log.w("class", schoolClass.startTime)
                    Log.w("class", schoolClass.subject)
                }
            }
            return timetableClassLL
        }

        fun generateTimetable(ctx: Context, currentTimetable: Map<SchoolDay, List<SchoolClass>>, timetableHolder: LinearLayout?, detailLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit, controller: MainController) {
            timetableHolder?.removeAllViews()
            for (day in currentTimetable) {
                val dayButton = Button(ctx)
                dayButton.text = day.key.toString()
                dayButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
                dayButton.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                dayButton.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                dayButton.setOnClickListener {
                    val timetableClassLL = generateSchoolClasses(ctx, day.value)
                    val timetableBackButton = Button(ctx)
                    timetableBackButton.text = "BACK"
                    timetableBackButton.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPrimary))
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