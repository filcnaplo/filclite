package com.thegergo02.minkreta.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.thegergo02.minkreta.kreta.KretaRequests
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.kreta.data.Student
import com.thegergo02.minkreta.kreta.data.homework.StudentHomework
import com.thegergo02.minkreta.kreta.data.homework.TeacherHomework
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.kreta.data.sub.Absence
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.kreta.data.timetable.Test
import com.thegergo02.minkreta.ui.*
import com.thegergo02.minkreta.view.MainView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.DayOfWeek
import java.time.LocalDateTime
import kotlin.math.abs


class MainActivity : AppCompatActivity(), MainView {
    private lateinit var controller: MainController
    private lateinit var cachedStudent: Student
    private var tabHolders = mutableMapOf<Tab, LinearLayout>()
    private var tabButtons = mutableMapOf<Tab, Button>()
    private var tabSortSpinners = mutableMapOf<Tab, Spinner>()

    private var canClick = true

    private lateinit var accessToken: String
    private lateinit var refreshToken: String
    private lateinit var instituteUrl: String
    private lateinit var instituteCode: String

    private var isHomeworkNeeded = false
    private var homeworkIds = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        controller = MainController(this, KretaRequests(this))

        val sharedPref = getSharedPreferences(getString(R.string.auth_path), Context.MODE_PRIVATE) ?: return

        val storedAccessToken = sharedPref.getString("accessToken", null)
        val storedRefreshToken = sharedPref.getString("refreshToken", null)
        val storedInstituteUrl = sharedPref.getString("instituteUrl", null)
        val storedInstituteCode = sharedPref.getString("instituteCode", null)

        if (storedAccessToken != null && storedRefreshToken != null && storedInstituteUrl != null && storedInstituteCode != null) {
            accessToken = storedAccessToken
            refreshToken = storedRefreshToken
            instituteUrl = storedInstituteUrl
            instituteCode = storedInstituteCode
        } else {
            sendToLogin()
        }
        initializeActivity()
    }

    private fun initializeActivity() {
        controller.getStudent(accessToken, instituteUrl)
        showProgress()
        setupHolders()
        setupSpinners()
        setupClickListeners()
    }

    private fun setupHolders() {
        tabHolders = mutableMapOf(
            Tab.Evaluations to eval_holder_ll,
            Tab.Notes to note_holder_ll,
            Tab.Absences to abs_holder_ll,
            Tab.Homework to homework_holder_ll,
            Tab.Timetable to timetable_holder_ll,
            Tab.Messages to messages_holder_ll,
            Tab.Tests to tests_holder_ll
        )
        tabButtons = mutableMapOf(
            Tab.Evaluations to evals_btt,
            Tab.Notes to notes_btt,
            Tab.Absences to abs_btt,
            Tab.Homework to homework_btt,
            Tab.Timetable to timetable_btt,
            Tab.Messages to messages_btt,
            Tab.Tests to tests_btt
        )
        tabSortSpinners = mutableMapOf(
            Tab.Notes to notes_spinner,
            Tab.Absences to abs_spinner
        )
    }
    private fun setupClickListeners() {
        name_tt.setOnClickListener {
            if (canClick) {
                if (details_ll.visibility == View.GONE) {
                    hideDetails()
                    val nameDetailsTextView = TextView(this)
                    nameDetailsTextView.text =
                        "(${cachedStudent.id}, ${cachedStudent.schoolYearId}) \n" +
                                "Place Of Birth: ${cachedStudent.placeOfBirth} \n" +
                                "Mother's name: ${cachedStudent.mothersName} \n" +
                                "AddressDataList: ${cachedStudent.addressDataList} \n" +
                                "DateOfBirthUTC: ${cachedStudent.DateOfBirthUtc} \n" +
                                "InstituteName: ${cachedStudent.instituteName} \n" +
                                "InstituteCode: ${cachedStudent.instituteCode} \n" +
                                "Lessons: ${cachedStudent.lessons} \n" +
                                "Events: ${cachedStudent.events}"
                    nameDetailsTextView.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.colorText
                        )
                    )
                    details_ll.addView(nameDetailsTextView)
                    showDetails()
                } else {
                    hideDetails()
                }
            }
        }
        tabButtons[Tab.Evaluations]?.setOnClickListener {
            if (canClick) {
                switchTab(Tab.Evaluations)
            }
        }
        tabButtons[Tab.Notes]?.setOnClickListener {
            if (canClick) {
                switchTab(Tab.Notes)
            }
        }
        tabButtons[Tab.Absences]?.setOnClickListener {
            if (canClick) {
                switchTab(Tab.Absences)
            }
        }
        tabButtons[Tab.Homework]?.setOnClickListener {
            if (canClick) {
                if (tabHolders[Tab.Homework]?.visibility == View.GONE) {
                    showProgress()
                    isHomeworkNeeded = true
                    startTimetableRequest()
                } else {
                    switchTab(Tab.Homework)
                }
            }
        }
        tabButtons[Tab.Timetable]?.setOnClickListener {
            if (canClick) {
                if (tabHolders[Tab.Timetable]?.visibility == View.GONE) {
                    showProgress()
                    startTimetableRequest()
                } else {
                    switchTab(Tab.Timetable)
                }
            }
        }
        tabButtons[Tab.Messages]?.setOnClickListener {
            if (canClick) {
                if (tabHolders[Tab.Messages]?.visibility == View.GONE) {
                    showProgress()
                    controller.getMessageList(accessToken)
                } else {
                    switchTab(Tab.Messages)
                }
            }
        }
        tabButtons[Tab.Tests]?.setOnClickListener {
            if (canClick) {
                if (tabHolders[Tab.Tests]?.visibility == View.GONE) {
                    showProgress()
                    controller.getTestList(accessToken, instituteUrl,
                        KretaDate(1970),
                        KretaDate()
                    )
                } else {
                    switchTab(Tab.Tests)
                }
            }
        }
    }

    private fun setupItemSelectedListener(spinnerPair: MutableMap.MutableEntry<Tab, Spinner>) {
        spinnerPair.value.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (spinnerPair.value.selectedItem.toString()) {
                    "" -> {}
                }
            }
        }
    }
    private fun setupSpinnerAdapter(spinnerPair: MutableMap.MutableEntry<Tab, Spinner>) {
        val spinnerDisplayArrayMap = mapOf(
            Tab.Notes to listOf("Date", "Type", "Teacher"),
            Tab.Absences to listOf("Subject", "Teacher", "Lesson start time", "Creating time", "Justification state")
        )
        val spinnerDisplayList = spinnerDisplayArrayMap[spinnerPair.key]
        if (spinnerDisplayList != null) {
            val adapter =
                ArrayAdapter(this, R.layout.sorter_spinner_item, spinnerDisplayList)
            adapter.setDropDownViewResource(R.layout.sorter_spinner_dropdown_item)
            spinnerPair.value.adapter = adapter
        }
    }
    private fun setupSpinners() {
        for (spinnerPair in tabSortSpinners) {
            setupSpinnerAdapter(spinnerPair)
            setupItemSelectedListener(spinnerPair)
        }
    }

    override fun hideProgress() {
        loading_bar.visibility = View.GONE
        name_tt.visibility = View.VISIBLE
        canClick = true
    }
    override fun showProgress() {
        loading_bar.visibility = View.VISIBLE
        name_tt.visibility = View.GONE
        canClick = false
    }

    override fun setStudent(student: Student) {
        cachedStudent = student
        refreshUI()
    }

    override fun displayError(error: String) {
        val errorSnack = Snackbar.make(main_cl, error, Snackbar.LENGTH_LONG)
        errorSnack.view.setBackgroundColor(ContextCompat.getColor(this,
            R.color.colorError
        ))
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
        Homework,
        Timetable,
        Messages,
        Tests
    }
    private fun closeTabs(exception: Tab? = null) {
        for (tabHolder in tabHolders) {
            if (tabHolder.key != exception) {
                tabHolder.value.visibility = View.GONE
                tabSortSpinners[tabHolder.key]?.visibility = View.GONE
                tabButtons[tabHolder.key]?.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.colorButtonUnselected
                ))
            }
        }
        hideDetails()
    }
    private fun switchTab(newTab: Tab) {
        closeTabs(newTab)
        val tabHolder = tabHolders[newTab]
        val tabButton = tabButtons[newTab]
        val tabSpinner = tabSortSpinners[newTab]
        var newVisibility: Int
        if (tabHolder != null && tabButton != null) {
            if (tabHolder.visibility == View.GONE) {
                newVisibility = View.VISIBLE
                tabButton.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.colorButtonSelected
                ))
            } else {
                newVisibility = View.GONE
                tabButton.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.colorButtonUnselected
                ))
            }
            tabHolder.visibility = newVisibility
            if (tabSpinner != null) {
                tabSpinner.visibility = newVisibility
            }
        }
    }

    override fun generateTimetable(timetable: Map<SchoolDay, List<SchoolClass>>) {
        if (isHomeworkNeeded) {
            populateHomeworkIds(timetable)
            isHomeworkNeeded = false
        } else {
            TimetableUI.generateTimetable(this, timetable,
                tabHolders[Tab.Timetable], details_ll, ::showDetails, ::hideDetails, controller)
            switchTab(Tab.Timetable)
            hideProgress()
        }
    }
    private fun populateHomeworkIds(timetable: Map<SchoolDay, List<SchoolClass>>) {
        for (schoolClassList in timetable.values) {
            for (schoolClass in schoolClassList) {
                if (schoolClass.teacherHomeworkId != null) {
                    homeworkIds.add(schoolClass.teacherHomeworkId)
                }
            }
        }
        controller.getHomework(accessToken, instituteUrl, homeworkIds)
    }

    override fun generateMessageDescriptors(messages: List<MessageDescriptor>) {
        MessageUI.generateMessageDescriptors(this, messages, tabHolders[Tab.Messages], controller, accessToken)
        switchTab(Tab.Messages)
        hideProgress()
    }
    override fun generateMessage(message: MessageDescriptor) {
        MessageUI.generateMessage(this, message.message, details_ll, ::showDetails, ::hideDetails)
    }

    override fun generateTests(testList: List<Test>) {
        TestUI.generateTests(this, testList, tabHolders[Tab.Tests], details_ll, ::showDetails, ::hideDetails)
        switchTab(Tab.Tests)
        hideProgress()
    }

    override fun generateHomeworkList(studentHomeworkList: List<StudentHomework>, teacherHomeworkList: List<TeacherHomework>) {
        tabHolders[Tab.Homework]?.removeAllViews()
        HomeworkUI.generateTeacherHomework(this, teacherHomeworkList, tabHolders[Tab.Homework], details_ll, ::showDetails, ::hideDetails)
        HomeworkUI.generateStudentHomework(this, studentHomeworkList, tabHolders[Tab.Homework], details_ll, ::showDetails, ::hideDetails)
        switchTab(Tab.Homework)
        homeworkIds = mutableListOf()
        hideProgress()
    }

    private fun refreshUI() {
        showProgress()
        closeTabs()
        name_tt.visibility = View.VISIBLE
        name_tt.text = cachedStudent.name
        EvaluationUI.generateEvaluations(this, cachedStudent,
            tabHolders[Tab.Evaluations], details_ll, ::showDetails, ::hideDetails)
        NotesUI.generateNotes(this, cachedStudent,
            tabHolders[Tab.Notes], details_ll, ::showDetails, ::hideDetails)
        val absenceList = cachedStudent.absences
        if (absenceList != null) {
            AbsencesUI.generateAbsences(
                this, absenceList.sortedWith(compareBy(Absence.SortType.Subject.lambda)),
                tabHolders[Tab.Absences], details_ll, ::showDetails, ::hideDetails
            )
        }
        hideProgress()
    }

    override fun triggerRefreshToken() {
        controller.refreshToken(refreshToken, instituteUrl, instituteCode)
    }
    override fun refreshToken(tokens: Map<String, String>) {
        val mainIntent = Intent(this, MainActivity::class.java)
        val sharedPref = getSharedPreferences("com.thegergo02.minkreta.auth", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("accessToken", tokens["access_token"])
            putString("refreshToken", tokens["refresh_token"])
            commit()
        }
        initializeActivity()
    }

    override fun sendToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    private fun startTimetableRequest() {
        val firstDay = LocalDateTime.now().with(DayOfWeek.MONDAY)
        val startDate = KretaDate(firstDay)
        val endDate = KretaDate(firstDay.plusDays(6))
        controller.getTimetable(
            accessToken,
            instituteUrl,
            startDate,
            endDate
        )
    }
}
