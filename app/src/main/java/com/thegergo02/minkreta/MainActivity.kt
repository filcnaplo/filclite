package com.thegergo02.minkreta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.data.Student
import com.thegergo02.minkreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.ui.*
import com.thegergo02.minkreta.view.MainView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


class MainActivity : AppCompatActivity(), MainView {
    private lateinit var controller: MainController
    private lateinit var cachedStudent: Student
    private lateinit var itemHolders: Map<Tab, LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        controller = MainController(this, ApiHandler(this))

        var passedAccessToken = intent.getStringExtra("access_token")
        var passedRefreshToken = intent.getStringExtra("refresh_token")
        var passedInstituteCode = intent.getStringExtra("institute_code")

        lateinit var accessToken: String
        lateinit var refreshToken: String
        lateinit var instituteCode: String

        if (passedAccessToken == null) {
            accessToken = accessToken
            refreshToken = refreshToken
            instituteCode = instituteCode
        } else {
            accessToken = passedAccessToken
            refreshToken = passedRefreshToken
            instituteCode = passedInstituteCode
        }

        controller.getStudent(accessToken, refreshToken, instituteCode)
        showProgress()

        itemHolders = mutableMapOf<Tab, LinearLayout>(
            Tab.Evaluations to eval_holder_ll,
            Tab.Notes to note_holder_ll,
            Tab.Absences to abs_holder_ll,
            Tab.Homeworks to homework_holder_ll,
            Tab.Timetable to timetable_holder_ll
        )

        name_tt.setOnClickListener {
            if (details_ll.visibility == View.GONE) {
                hideDetails()
                val nameDetailsTextView = TextView(this)
                nameDetailsTextView.text = "ID: ${cachedStudent.id} \n" +
                        "SchoolYearID: ${cachedStudent.schoolYearId} \n" +
                        "Name: ${cachedStudent.name} \n" +
                        "NameOfBirth: ${cachedStudent.nameOfBirth} \n" +
                        "PlaceOfBirth: ${cachedStudent.placeOfBirth} \n" +
                        "Mother's name: ${cachedStudent.mothersName} \n" +
                        "AddressDataList: ${cachedStudent.addressDataList} \n" +
                        "DateOfBirtUTC: ${cachedStudent.DateOfBirthUtc} \n" +
                        "InstituteName: ${cachedStudent.instituteName} \n" +
                        "InstituteCode: ${cachedStudent.instituteCode} \n" +
                        "Lessons: ${cachedStudent.lessons} \n" +
                        "Events: ${cachedStudent.events}"
                nameDetailsTextView.setTextColor(ContextCompat.getColor(this, R.color.colorText))
                details_ll.addView(nameDetailsTextView)
                showDetails()
            } else {
                hideDetails()
            }
        }

        evals_btt.setOnClickListener {
            switchTab(Tab.Evaluations)
        }
        notes_btt.setOnClickListener {
            switchTab(Tab.Notes)
        }
        abs_btt.setOnClickListener {
            switchTab(Tab.Absences)
        }
        homeworks_btt.setOnClickListener {
            switchTab(Tab.Homeworks)
        }
        timetable_btt.setOnClickListener {
            if (itemHolders.get(Tab.Timetable)?.visibility == View.GONE) {
                showProgress()
                val firstDay = LocalDateTime.now().with(DayOfWeek.MONDAY)
                val startDate = KretaDate(firstDay)
                val endDate = KretaDate(firstDay.plusDays(6))
                Log.w("date", startDate.toString())
                Log.w("date", endDate.toString())
                controller.getTimetable(
                    accessToken,
                    refreshToken,
                    instituteCode,
                    startDate,
                    endDate
                )
            } else {
                switchTab(Tab.Timetable)
            }
        }
    }
    
    override fun hideProgress() {
        loading_bar.visibility = View.GONE
        name_tt.visibility = View.VISIBLE
    }
    override fun showProgress() {
        loading_bar.visibility = View.VISIBLE
        name_tt.visibility = View.GONE
    }

    override fun setStudent(student: Student) {
        cachedStudent = student
        refreshUI()
    }

    override fun displayError(error: String) {
        val errorSnack = Snackbar.make(main_cl, error, Snackbar.LENGTH_LONG)
        errorSnack.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorError))
        errorSnack.show()
    }

    private fun showDetails() {
        details_ll.visibility = View.VISIBLE
        scroll_view.smoothScrollTo(0, 0)
    }
    private fun hideDetails() {
        details_ll.visibility = View.GONE
        details_ll.removeAllViews()
    }

    enum class Tab {
        Evaluations,
        Notes,
        Absences,
        Homeworks,
        Timetable,
        Messages
    }

    private fun closeTabs(exception: Tab? = null) {
        for (tabHolder in itemHolders) {
            if (tabHolder.key != exception) {
                tabHolder.value.visibility = View.GONE
            }
        }
    }
    private fun switchTab(newTab: Tab) {
        closeTabs(newTab)
        val tabHolder = itemHolders.get(newTab)
        if (tabHolder?.visibility == View.GONE) {
            tabHolder?.visibility = View.VISIBLE
        } else {
            tabHolder?.visibility = View.GONE
        }
    }

    override fun generateTimetable(timetable: Map<SchoolDay, List<SchoolClass>>) {
        TimetableUI.generateTimetable(this, timetable, itemHolders.get(Tab.Timetable), details_ll, ::showDetails, ::hideDetails, controller)
        switchTab(Tab.Timetable)
        hideProgress()
    }

    private fun refreshUI() {
        showProgress()
        closeTabs()
        name_tt.visibility = View.VISIBLE
        name_tt.text = cachedStudent.name
        EvaluationUI.generateEvaluations(this, cachedStudent, itemHolders.get(Tab.Evaluations), details_ll, ::showDetails, ::hideDetails)
        NotesUI.generateNotes(this, cachedStudent, itemHolders.get(Tab.Notes), details_ll, ::showDetails, ::hideDetails)
        AbsencesUI.generateAbsences(this, cachedStudent, itemHolders.get(Tab.Absences), details_ll, ::showDetails, ::hideDetails)
        HomeworksUI.generateHomeworks(this, cachedStudent, itemHolders.get(Tab.Homeworks), details_ll, ::showDetails, ::hideDetails)
        hideProgress()
    }
}
