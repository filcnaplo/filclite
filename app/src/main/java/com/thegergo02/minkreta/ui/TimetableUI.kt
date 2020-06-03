package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.ui.manager.RefreshableData

class TimetableUI {
    companion object {
        private fun generateSchoolClasses(ctx: Context, classes: List<SchoolClass>, detailsLL: LinearLayout, toggleDetails: (Boolean) -> Unit): LinearLayout {
            val timetableClassLL = LinearLayout(ctx)
            timetableClassLL.orientation = LinearLayout.VERTICAL
            for (schoolClass in classes) {
                val text = schoolClass.toString()
                val classOnClickListener = {
                    _: View, _: RefreshableData ->
                    val classDetailsTextView = TextView(ctx)
                    classDetailsTextView.text = schoolClass.toDetailedString()
                    listOf(classDetailsTextView)
                }
                val classButton = UIHelper.generateButton(ctx, text, classOnClickListener, RefreshableData(""), toggleDetails, detailsLL)
                timetableClassLL.addView(classButton)
            }
            return timetableClassLL
        }

        fun generateTimetable(ctx: Context,
                              currentTimetable: Map<SchoolDay, List<SchoolClass>>,
                              timetableHolder: LinearLayout?,
                              detailsLL: LinearLayout,
                              toggleDetails: (Boolean) -> Unit,
                              controller: MainController,
                              themeHelper: ThemeHelper) {
            timetableHolder?.removeAllViews()
            for (day in currentTimetable) {
                val text = day.key.toString()
                val textColor = if (KretaDate().fromSchoolDay(day.key).isToday()) {
                    themeHelper.getColorFromAttributes(R.attr.colorText)
                } else {
                    themeHelper.getColorFromAttributes(R.attr.colorUnavailable)
                }
                val dayOnClickListener = {
                    _: View, _: RefreshableData ->
                    val backText = ctx.getString(R.string.back_ma)
                    val timetableBackOnClickListener = {
                        _: View, _: RefreshableData ->
                        generateTimetable(ctx, currentTimetable, timetableHolder, detailsLL, toggleDetails, controller, themeHelper)
                        listOf<View>()
                    }
                    timetableHolder?.removeAllViews()
                    val timetableBackButton = UIHelper.generateButton(ctx, backText, timetableBackOnClickListener, RefreshableData(""), toggleDetails, detailsLL)
                    timetableBackButton.setTextColor(themeHelper.getColorFromAttributes(R.attr.colorUnavailable))
                    timetableHolder?.addView(timetableBackButton)
                    val timetableClassLL = generateSchoolClasses(ctx, day.value, detailsLL, toggleDetails)
                    timetableHolder?.addView(timetableClassLL)
                    listOf<View>()
                }
                val dayButton = UIHelper.generateButton(ctx, text, dayOnClickListener, RefreshableData(""), toggleDetails, detailsLL)
                dayButton.setTextColor(textColor)
                timetableHolder?.addView(dayButton)
            }
        }
    }
}