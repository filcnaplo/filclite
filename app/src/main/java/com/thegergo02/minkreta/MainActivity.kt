package com.thegergo02.minkreta

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.data.Student
import com.thegergo02.minkreta.data.message.Message
import com.thegergo02.minkreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.ui.*
import com.thegergo02.minkreta.view.MainView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.DayOfWeek
import java.time.LocalDateTime


class MainActivity : AppCompatActivity(), MainView {
    private lateinit var controller: MainController
    private lateinit var cachedStudent: Student
    private lateinit var itemHolders: Map<Tab, LinearLayout>
    private lateinit var tabButtons: Map<Tab, Button>

    private lateinit var accessToken: String
    private lateinit var refreshToken: String
    private lateinit var instituteCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        controller = MainController(this, ApiHandler(this))

        val passedAccessToken = intent.getStringExtra("access_token")
        val passedRefreshToken = intent.getStringExtra("refresh_token")
        val passedInstituteCode = intent.getStringExtra("institute_code")

        if (passedAccessToken != null) {
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
            Tab.Timetable to timetable_holder_ll,
            Tab.Messages to messages_holder_ll
        )
        tabButtons = mutableMapOf<Tab, Button>(
            Tab.Evaluations to evals_btt,
            Tab.Notes to notes_btt,
            Tab.Absences to abs_btt,
            Tab.Homeworks to homework_btt,
            Tab.Timetable to timetable_btt,
            Tab.Messages to messages_btt
        )

        name_tt.setOnClickListener {
            if (details_ll.visibility == View.GONE) {
                hideDetails()
                val nameDetailsTextView = TextView(this)
                nameDetailsTextView.text = "(${cachedStudent.id}, ${cachedStudent.schoolYearId}) \n" +
                        "Place Of Birth: ${cachedStudent.placeOfBirth} \n" +
                        "Mother's name: ${cachedStudent.mothersName} \n" +
                        "AddressDataList: ${cachedStudent.addressDataList} \n" +
                        "DateOfBirthUTC: ${cachedStudent.DateOfBirthUtc} \n" +
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
        homework_btt.setOnClickListener {
            switchTab(Tab.Homeworks)
        }
        timetable_btt.setOnClickListener {
            if (itemHolders[Tab.Timetable]?.visibility == View.GONE) {
                showProgress()
                val firstDay = LocalDateTime.now().with(DayOfWeek.MONDAY)
                val startDate = KretaDate(firstDay)
                val endDate = KretaDate(firstDay.plusDays(6))
                controller.getTimetable(
                    accessToken,
                    instituteCode,
                    startDate,
                    endDate
                )
            } else {
                switchTab(Tab.Timetable)
            }
        }
        messages_btt.setOnClickListener {
            if (itemHolders[Tab.Messages]?.visibility == View.GONE) {
                showProgress()
                controller.getMessageList(accessToken)
            } else {
                switchTab(Tab.Messages)
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
                tabButtons[tabHolder.key]?.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }
        }
        hideDetails()
    }
    private fun switchTab(newTab: Tab) {
        closeTabs(newTab)
        val tabHolder = itemHolders[newTab]
        val tabButton = tabButtons[newTab]
        if (tabHolder != null && tabButton != null) {
            if (tabHolder.visibility == View.GONE) {
                tabHolder.visibility = View.VISIBLE
                tabButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
            } else {
                tabHolder.visibility = View.GONE
                tabButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            }
        }
    }

    override fun generateTimetable(timetable: Map<SchoolDay, List<SchoolClass>>) {
        TimetableUI.generateTimetable(this, timetable,
            itemHolders[Tab.Timetable], details_ll, ::showDetails, ::hideDetails, controller)
        switchTab(Tab.Timetable)
        hideProgress()
    }

    override fun generateMessageDescriptors(messages: List<MessageDescriptor>) {
        MessageUI.generateMessageDescriptors(this, messages, itemHolders[Tab.Messages], controller, accessToken)
        switchTab(Tab.Messages)
        hideProgress()
    }

    override fun generateMessage(message: Message) {
        MessageUI.generateMessage(this, message, details_ll, ::showDetails, ::hideDetails)
    }

    private fun refreshUI() {
        showProgress()
        closeTabs()
        name_tt.visibility = View.VISIBLE
        name_tt.text = cachedStudent.name
        EvaluationUI.generateEvaluations(this, cachedStudent,
            itemHolders[Tab.Evaluations], details_ll, ::showDetails, ::hideDetails)
        NotesUI.generateNotes(this, cachedStudent,
            itemHolders[Tab.Notes], details_ll, ::showDetails, ::hideDetails)
        AbsencesUI.generateAbsences(this, cachedStudent,
            itemHolders[Tab.Absences], details_ll, ::showDetails, ::hideDetails)
        HomeworkUI.generateHomework(this, listOf(),
            itemHolders[Tab.Homeworks], details_ll, ::showDetails, ::hideDetails)
        hideProgress()
    }
}
