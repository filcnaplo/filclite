package com.thegergo02.minkreta.activity

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.thegergo02.minkreta.kreta.KretaRequests
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.controller.MainController
import com.thegergo02.minkreta.kreta.StudentDetails
import com.thegergo02.minkreta.kreta.data.homework.Homework
import com.thegergo02.minkreta.kreta.data.homework.HomeworkComment
import com.thegergo02.minkreta.kreta.data.message.Attachment
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.kreta.data.sub.Absence
import com.thegergo02.minkreta.kreta.data.sub.Evaluation
import com.thegergo02.minkreta.kreta.data.sub.Note
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.kreta.data.timetable.Test
import com.thegergo02.minkreta.ui.*
import com.thegergo02.minkreta.view.MainView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.DayOfWeek
import java.time.LocalDateTime


class MainActivity : AppCompatActivity(), MainView {
    private lateinit var controller: MainController
    private var tabHolders = mutableMapOf<Tab, LinearLayout>()
    private var tabButtons = mutableMapOf<Tab, Button>()
    private var tabSortSpinners = mutableMapOf<Tab, Spinner>()

    private var canClick = true

    private lateinit var accessToken: String
    private lateinit var refreshToken: String
    private lateinit var instituteUrl: String
    private lateinit var instituteCode: String

    private var abs = listOf<Absence>()
    private var notes = listOf<Note>()
    private var evals = listOf<Evaluation>()
    private var homeworks = listOf<Homework>()

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
        setupHolders()
        setupSpinners()
        setupClickListeners()
        controller.getStudentDetails(accessToken, instituteUrl)
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
            Tab.Absences to abs_spinner,
            Tab.Messages to messages_spinner,
            Tab.Evaluations to evals_spinner,
            Tab.Homework to homework_spinner
        )
    }
    private fun setupClickListeners() {
        canClick = true
        name_tt.setOnClickListener {
            if (canClick) {
                if (details_ll.visibility == View.GONE) {
                    controller.getStudentDetails(accessToken, instituteUrl)
                }
                hideDetails()
            }
        }
        tabButtons[Tab.Evaluations]?.setOnClickListener {
            if (canClick) {
                if (tabHolders[Tab.Evaluations]?.visibility == View.GONE) {
                    showProgress()
                    controller.getEvaluationList(accessToken, instituteUrl)
                } else {
                    switchTab(Tab.Evaluations)
                }
            }
        }
        tabButtons[Tab.Notes]?.setOnClickListener {
            if (canClick) {
                if (tabHolders[Tab.Notes]?.visibility == View.GONE) {
                    showProgress()
                    controller.getNoteList(accessToken, instituteUrl)
                } else {
                    switchTab(Tab.Notes)
                }
            }
        }
        tabButtons[Tab.Absences]?.setOnClickListener {
            if (canClick) {
                if (tabHolders[Tab.Absences]?.visibility == View.GONE) {
                    showProgress()
                    controller.getAbsenceList(accessToken, instituteUrl)
                } else {
                    switchTab(Tab.Absences)
                }
            }
        }
        tabButtons[Tab.Homework]?.setOnClickListener {
            if (canClick) {
                if (tabHolders[Tab.Homework]?.visibility == View.GONE) {
                    showProgress()
                    val firstDay = LocalDateTime.now().with(DayOfWeek.MONDAY)
                    val startDate = KretaDate(firstDay)
                    controller.getHomeworkList(accessToken, instituteUrl, startDate)
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
                    refreshMessages(MessageDescriptor.SortType.SendDate)
                } else {
                    switchTab(Tab.Messages)
                }
            }
        }
        tabButtons[Tab.Tests]?.setOnClickListener {
            if (canClick) {
                if (tabHolders[Tab.Tests]?.visibility == View.GONE) {
                    showProgress()
                    controller.getTestList(accessToken, instituteUrl)
                } else {
                    switchTab(Tab.Tests)
                }
            }
        }
    }

    private fun setupItemSelectedListener(spinnerPair: MutableMap.MutableEntry<Tab, Spinner>) {
        var onItemSelectedListener: AdapterView.OnItemSelectedListener? = null
        when (spinnerPair.key) {
            Tab.Absences -> {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val sortType = Absence.sortTypeFromString(spinnerPair.value.selectedItem.toString())
                        refreshAbsences(sortType)
                    }
                }
            }
            Tab.Notes -> {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val sortType = Note.sortTypeFromString(spinnerPair.value.selectedItem.toString())
                        refreshNotes(sortType)
                    }
                }
            }
            Tab.Messages -> {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val sortType =
                            MessageDescriptor.sortTypeFromString(spinnerPair.value.selectedItem.toString())
                        refreshMessages(sortType)
                    }
                }
            }
            Tab.Evaluations -> {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val sortType = Evaluation.sortTypeFromString(spinnerPair.value.selectedItem.toString())
                        refreshEvaluations(sortType)
                    }
                }
            }
            Tab.Homework -> {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val sortType = Homework.sortTypeFromString(spinnerPair.value.selectedItem.toString())
                        refreshHomeworks(sortType)
                    }
                }
            }
        }
        if (onItemSelectedListener != null) {
            spinnerPair.value.onItemSelectedListener = onItemSelectedListener
        }
    }
    private fun setupSpinnerAdapter(spinnerPair: MutableMap.MutableEntry<Tab, Spinner>) {
        val spinnerDisplayArrayMap = mapOf(
            Tab.Notes to listOf("Date", "Type", "Teacher"),
            Tab.Absences to listOf("Subject", "Teacher", "Lesson start time", "Creating time", "Justification state"),
            Tab.Messages to listOf("Send date", "Teacher"),
            Tab.Evaluations to listOf("Creating time", "Form", "Value", "Mode", "Subject", "Teacher"),
            Tab.Homework to listOf("Post date", "Deadline", "Teacher", "Subject")
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
    private fun switchTab(newTab: Tab, canClose: Boolean = true) {
        closeTabs(newTab)
        val tabHolder = tabHolders[newTab]
        val tabButton = tabButtons[newTab]
        val tabSpinner = tabSortSpinners[newTab]
        val newVisibility: Int
        if (tabHolder != null && tabButton != null) {
            if (tabHolder.visibility == View.GONE) {
                newVisibility = View.VISIBLE
                tabButton.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.colorButtonSelected
                ))
            } else {
                if (canClose) {
                    newVisibility = View.GONE
                    tabButton.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.colorButtonUnselected
                        )
                    )
                } else {
                    newVisibility = View.VISIBLE
                }
            }
            tabHolder.visibility = newVisibility
            if (tabSpinner != null) {
                tabSpinner.visibility = newVisibility
            }
        }
    }

    override fun generateTimetable(timetable: Map<SchoolDay, List<SchoolClass>>) {
        TimetableUI.generateTimetable(this, timetable,
            tabHolders[Tab.Timetable], details_ll, ::showDetails, ::hideDetails, controller)
        switchTab(Tab.Timetable)
        hideProgress()
    }
    override fun generateMessageDescriptors(messages: List<MessageDescriptor>) {
        MessageUI.generateMessageDescriptors(this, messages, tabHolders[Tab.Messages], controller, accessToken)
        switchTab(Tab.Messages, false)
        hideProgress()
    }
    override fun generateMessage(message: MessageDescriptor) {
        MessageUI.generateMessage(this, message.message, details_ll, ::downloadAttachment, ::showDetails, ::hideDetails)
    }

    override fun generateTestList(testList: List<Test>) {
        tabHolders[Tab.Tests]?.removeAllViews()
        TestUI.generateTests(this, testList, tabHolders[Tab.Tests], details_ll, ::showDetails, ::hideDetails)
        switchTab(Tab.Tests)
        hideProgress()
    }

    override fun generateNoteList(notesS: List<Note>) {
        notes = notesS
        refreshNotes()
        switchTab(Tab.Notes)
        hideProgress()
    }

    override fun generateHomeworkList(homeworksS: List<Homework>) {
        homeworks = homeworksS
        refreshHomeworks()
        switchTab(Tab.Homework)
        hideProgress()
    }

    override fun generateHomeworkCommentList(homeworkComments: List<HomeworkComment>) {
        HomeworkUI.generateHomeworkCommentList(this, homeworkComments, details_ll)
    }

    override fun generateEvaluationList(evaluations: List<Evaluation>) {
        evals = evaluations
        refreshEvaluations()
        switchTab(Tab.Evaluations)
        hideProgress()
    }

    override fun generateAbsenceList(absences: List<Absence>) {
        abs = absences
        refreshAbsences()
        switchTab(Tab.Absences)
        hideProgress()
    }

    override fun generateStudentDetails(studentDetails: StudentDetails) {
        if (name_tt.text == "") {
            name_tt.text = studentDetails.toString()
        } else {
            val nameDetailsTextView = TextView(this)
            nameDetailsTextView.text = studentDetails.toDetailedString()
            nameDetailsTextView.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorText
                )
            )
            details_ll.addView(nameDetailsTextView)
            showDetails()
        }
    }

    private fun refreshHomeworks(sortType: Homework.SortType = Homework.SortType.PostDate) {
        val holder = tabHolders[Tab.Homework]
        holder?.removeAllViews()
        HomeworkUI.generateHomeworkList(this, homeworks.sortedWith(compareBy(sortType.lambda)), tabHolders[Tab.Homework], details_ll, ::showDetails, ::hideDetails,
            ::triggerGetHomeworkCommentList, ::sendHomeworkComment)
    }
    private fun refreshEvaluations(sortType: Evaluation.SortType = Evaluation.SortType.CreatingDate) {
        val holder = tabHolders[Tab.Evaluations]
        holder?.removeAllViews()
        EvaluationUI.generateEvaluations(
            this, evals.sortedWith(compareBy(sortType.lambda)),
            holder, details_ll, ::showDetails, ::hideDetails
        )
    }
    private fun refreshMessages(sortType: MessageDescriptor.SortType) {
        showProgress()
        controller.getMessageList(accessToken, sortType)
    }
    private fun refreshAbsences(sortType: Absence.SortType = Absence.SortType.Date) {
        val holder = tabHolders[Tab.Absences]
        holder?.removeAllViews()
        AbsencesUI.generateAbsences(
            this, abs.sortedWith(compareBy(sortType.lambda)),
            holder, details_ll, ::showDetails, ::hideDetails
        )
    }
    private fun refreshNotes(sortType: Note.SortType = Note.SortType.Date) {
        val holder = tabHolders[Tab.Notes]
        holder?.removeAllViews()
        NotesUI.generateNotes(
            this, notes.sortedWith(compareBy(sortType.lambda)),
            holder, details_ll, ::showDetails, ::hideDetails
        )
    }
    /*private fun refreshUI() {
        showProgress()
        closeTabs()
        name_tt.visibility = View.VISIBLE
        name_tt.text = cachedStudent.name
        refreshEvaluations(Evaluation.SortType.CreatingTime)
        refreshNotes(Note.SortType.CreatingTime)
        refreshAbsences(Absence.SortType.Subject)
        hideProgress()
    }*/

    override fun triggerRefreshToken() {
        controller.refreshToken(refreshToken, instituteCode)
    }
    private fun triggerGetHomeworkCommentList(homeworkUid: String) {
        controller.getHomeworkCommentList(accessToken, instituteUrl, homeworkUid)
    }
    private fun sendHomeworkComment(homeworkUid: String, text: String) {
        controller.sendHomeworkComment(accessToken, instituteUrl, homeworkUid, text)
    }
    override fun refreshToken(tokens: Map<String, String>) {
        val sharedPref = getSharedPreferences("com.thegergo02.minkreta.auth", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("accessToken", tokens["access_token"])
            putString("refreshToken", tokens["refresh_token"])
            commit()
        }
        initializeActivity() //LONG REFRESH
    }

    private fun downloadAttachment(attachment: Attachment) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        runWithPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            controller.downloadAttachment(accessToken, downloadManager, attachment)
        }
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
