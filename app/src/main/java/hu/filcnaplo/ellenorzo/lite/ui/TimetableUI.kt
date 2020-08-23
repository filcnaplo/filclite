package hu.filcnaplo.ellenorzo.lite.ui

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate
import hu.filcnaplo.ellenorzo.lite.R
import hu.filcnaplo.ellenorzo.lite.controller.MainController
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.MessageDescriptor
import hu.filcnaplo.ellenorzo.lite.kreta.data.timetable.SchoolClass
import hu.filcnaplo.ellenorzo.lite.kreta.data.timetable.SchoolDay
import hu.filcnaplo.ellenorzo.lite.ui.manager.RefreshableData
import java.util.*

class TimetableUI {
    companion object {
        private fun generateSchoolClasses(ctx: Context, classes: List<SchoolClass>, detailsLL: LinearLayout, toggleDetails: (Boolean) -> Unit, themeHelper: ThemeHelper): LinearLayout {
            val timetableClassLL = LinearLayout(ctx)
            timetableClassLL.orientation = LinearLayout.VERTICAL
            for (schoolClass in classes) {
                var text = schoolClass.toString()
                var detailsText = schoolClass.toDetailedString()
                val textColor = if (schoolClass.state.uid == "4,TanevRendjeEsemeny") {
                    text = schoolClass.name
                    detailsText = schoolClass.name + "\n" + schoolClass.state.description
                    themeHelper.getColorFromAttributes(R.attr.colorAccent)
                } else {
                    themeHelper.getColorFromAttributes(R.attr.colorText)
                }
                val classOnClickListener = {
                        _: View, _: RefreshableData ->
                    val classDetailsTextView = TextView(ctx)
                    classDetailsTextView.text = detailsText
                    listOf(classDetailsTextView)
                }
                val classButton = UIHelper.generateButton(ctx, text, classOnClickListener, RefreshableData(""), toggleDetails, detailsLL)
                classButton.setTextColor(textColor)
                timetableClassLL.addView(classButton)
            }
            return timetableClassLL
        }

        fun generateTimetable(ctx: Context,
                              currentTimetable: Map<SchoolDay, List<SchoolClass>>?,
                              timetableHolder: LinearLayout?,
                              detailsLL: LinearLayout,
                              toggleDetails: (Boolean) -> Unit,
                              setFromDate: (Int, Int, Int) -> Unit,
                              controller: MainController,
                              themeHelper: ThemeHelper) {
            timetableHolder?.removeAllViews()
            val fromCalendar = Calendar.getInstance()
            val fromDialog = DatePickerDialog(ctx, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                setFromDate(year, month, day)
            }, fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.DAY_OF_MONTH))
            val fromButton = UIHelper.generateButton(ctx, ctx.getString(R.string.from_ma), { _, _ -> fromDialog.show(); listOf()})
            timetableHolder?.addView(fromButton)
            if (currentTimetable != null) {
                for (day in currentTimetable) {
                    val schoolDayToString = mapOf(
                        SchoolDay.Monday to ctx.getString(R.string.monday_ma),
                        SchoolDay.Tuesday to ctx.getString(R.string.tuesday_ma),
                        SchoolDay.Wednesday to ctx.getString(R.string.wednesday_ma),
                        SchoolDay.Thursday to ctx.getString(R.string.thursday_ma),
                        SchoolDay.Friday to ctx.getString(R.string.friday_ma),
                        SchoolDay.Saturday to ctx.getString(R.string.saturday_ma),
                        SchoolDay.Sunday to ctx.getString(R.string.sunday_ma)
                    )
                    val text = schoolDayToString[day.key] ?: ctx.getString(R.string.monday_ma)
                    val textColor = if (KretaDate().fromSchoolDay(day.key).isToday()) {
                        themeHelper.getColorFromAttributes(R.attr.colorText)
                    } else {
                        themeHelper.getColorFromAttributes(R.attr.colorUnavailable)
                    }
                    val dayOnClickListener = { _: View, _: RefreshableData ->
                        val backText = ctx.getString(R.string.back_ma)
                        val timetableBackOnClickListener = { _: View, _: RefreshableData ->
                            generateTimetable(
                                ctx,
                                currentTimetable,
                                timetableHolder,
                                detailsLL,
                                toggleDetails,
                                setFromDate,
                                controller,
                                themeHelper
                            )
                            listOf<View>()
                        }
                        timetableHolder?.removeAllViews()
                        val timetableBackButton = UIHelper.generateButton(
                            ctx,
                            backText,
                            timetableBackOnClickListener,
                            RefreshableData(""),
                            toggleDetails,
                            detailsLL
                        )
                        timetableBackButton.setTextColor(themeHelper.getColorFromAttributes(R.attr.colorUnavailable))
                        timetableHolder?.addView(timetableBackButton)
                        val timetableClassLL = generateSchoolClasses(
                            ctx,
                            day.value,
                            detailsLL,
                            toggleDetails,
                            themeHelper
                        )
                        timetableHolder?.addView(timetableClassLL)
                        listOf<View>()
                    }
                    val dayButton = UIHelper.generateButton(
                        ctx,
                        text,
                        dayOnClickListener,
                        RefreshableData(""),
                        toggleDetails,
                        detailsLL
                    )
                    dayButton.setTextColor(textColor)
                    timetableHolder?.addView(dayButton)
                }
            } else {
                val emptyText = TextView(ctx)
                emptyText.text = ctx.getString(R.string.empty_timetable_ma)
                timetableHolder?.addView(emptyText)
            }
        }
    }
}