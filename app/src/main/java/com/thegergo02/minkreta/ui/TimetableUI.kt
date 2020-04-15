package com.thegergo02.minkreta.ui

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay

class TimetableUI {
    companion object {
        private fun generateSchoolClasses(ctx: Context, classes: List<SchoolClass>, detailsLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit): LinearLayout {
            val timetableClassLL = LinearLayout(ctx)
            timetableClassLL.orientation = LinearLayout.VERTICAL
            for (schoolClass in classes) {
                val text = schoolClass.toString()
                val classOnClickListener = {
                    _: View ->
                    val classDetailsTextView = TextView(ctx)
                    classDetailsTextView.text = schoolClass.toDetailedString()
                    classDetailsTextView.setTextColor(ContextCompat.getColor(ctx, R.color.colorText))
                    listOf(classDetailsTextView)
                }
                val classButton = UIHelper.generateButton(ctx, text, classOnClickListener, showDetails, hideDetails, detailsLL)
                timetableClassLL.addView(classButton)
            }
            return timetableClassLL
        }

        fun generateTimetable(ctx: Context, currentTimetable: Map<SchoolDay, List<SchoolClass>>, timetableHolder: LinearLayout?, detailsLL: LinearLayout, showDetails: () -> Unit, hideDetails: () -> Unit, controller: MainController) {
            timetableHolder?.removeAllViews()
            for (day in currentTimetable) {
                val text = day.key.toString()
                val textColor = if (KretaDate().fromSchoolDay(day.key).isToday()) {
                    R.color.colorText
                } else {
                    R.color.colorUnavailable
                }
                val dayOnClickListener = {
                    _: View ->
                    val text = ctx.getString(R.string.back_ma)
                    val timetableBackOnClickListener = {
                        _: View ->
                        generateTimetable(ctx, currentTimetable, timetableHolder, detailsLL, showDetails, hideDetails, controller)
                        listOf<View>()
                    }
                    timetableHolder?.removeAllViews()
                    val timetableBackButton = UIHelper.generateButton(ctx, text, timetableBackOnClickListener, showDetails, hideDetails, detailsLL, textColor)
                    timetableHolder?.addView(timetableBackButton)
                    val timetableClassLL = generateSchoolClasses(ctx, day.value, detailsLL, showDetails, hideDetails)
                    timetableHolder?.addView(timetableClassLL)
                    listOf<View>()
                }
                val dayButton = UIHelper.generateButton(ctx, text, dayOnClickListener, showDetails, hideDetails, detailsLL, textColor)
                timetableHolder?.addView(dayButton)
            }
        }
    }
}