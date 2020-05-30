package com.thegergo02.minkreta.activity

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
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
import com.thegergo02.minkreta.kreta.data.sub.Notice
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.kreta.data.timetable.Test
import com.thegergo02.minkreta.ui.*
import com.thegergo02.minkreta.view.MainView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.DayOfWeek
import java.time.LocalDateTime


class MainActivity : AppCompatActivity(), MainView {
    @ColorInt
    fun getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }
    private lateinit var controller: MainController
    private var tabHolders = mutableMapOf<Tab, LinearLayout>()
    private var tabButtons = mutableMapOf<Tab, Button>()
    private var tabOnClickListeners = mutableMapOf<Tab, (View) -> Unit>()
    private var tabSortSpinners = mutableMapOf<Tab, Spinner>()

    private var canClick = true

    private var abs = listOf<Absence>()
    private var notes = listOf<Note>()
    private var evals = listOf<Evaluation>()
    private var homeworks = listOf<Homework>()
    private var notices = listOf<Notice>()
    private lateinit var homeworkCommentHolder: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefTheme = getSharedPreferences(getString(R.string.theme_path), Context.MODE_PRIVATE) ?: return
        val isDark = sharedPrefTheme.getBoolean("dark", true)
        if (!isDark) {
            setTheme(R.style.LightTheme)
        }
        setContentView(R.layout.activity_main)

        val sharedPrefAuth = getSharedPreferences(getString(R.string.auth_path), Context.MODE_PRIVATE) ?: return
        val accessToken = sharedPrefAuth.getString("accessToken", null)
        val refreshToken = sharedPrefAuth.getString("refreshToken", null)
        val instituteCode = sharedPrefAuth.getString("instituteCode", null)
        if (accessToken != null && refreshToken != null && instituteCode != null) {
            controller = MainController(this, this, accessToken, refreshToken, instituteCode)
        } else {
            sendToLogin()
        }
        initializeActivity()
    }

    private fun initializeActivity() {
        setupHolders()
        setupTabHolders()
        setupSpinners()
        setupClickListeners()
        homeworkCommentHolder = LinearLayout(this)
        controller.getStudentDetails()
    }

    private fun setupHolders() {
        tabHolders = mutableMapOf(
            Tab.Evaluations to eval_holder_ll,
            Tab.Notes to note_holder_ll,
            Tab.Absences to abs_holder_ll,
            Tab.Homework to homework_holder_ll,
            Tab.Timetable to timetable_holder_ll,
            Tab.Messages to messages_holder_ll,
            Tab.Tests to tests_holder_ll,
            Tab.Noticeboard to noticeboard_holder_ll
        )
        tabButtons = mutableMapOf(
            Tab.Evaluations to evals_btt,
            Tab.Notes to notes_btt,
            Tab.Absences to abs_btt,
            Tab.Homework to homework_btt,
            Tab.Timetable to timetable_btt,
            Tab.Messages to messages_btt,
            Tab.Tests to tests_btt,
            Tab.Noticeboard to noticeboard_btt
        )
        tabOnClickListeners = mutableMapOf( //THEY ARE NOT REDUNDANT, IDE IS AN IDIOT THX
            Tab.Evaluations to { _ ->
                if (canClick) {
                    if (tabHolders[Tab.Evaluations]?.visibility == View.GONE) {
                        showProgress()
                        controller.getEvaluationList()
                    } else {
                        switchTab(Tab.Evaluations)
                    }
                }
            },
            Tab.Notes to { _ ->
                if (canClick) {
                    if (tabHolders[Tab.Notes]?.visibility == View.GONE) {
                        showProgress()
                        controller.getNoteList()
                    } else {
                        switchTab(Tab.Notes)
                    }
                }
            },
            Tab.Absences to { _ ->
                if (canClick) {
                    if (tabHolders[Tab.Absences]?.visibility == View.GONE) {
                        showProgress()
                        controller.getAbsenceList()
                    } else {
                        switchTab(Tab.Absences)
                    }
                }
            },
            Tab.Homework to { _ ->
                if (canClick) {
                    if (tabHolders[Tab.Homework]?.visibility == View.GONE) {
                        showProgress()
                        val firstDay = LocalDateTime.now().with(DayOfWeek.MONDAY)
                        val startDate = KretaDate(firstDay)
                        controller.getHomeworkList(startDate)
                    } else {
                        switchTab(Tab.Homework)
                    }
                }
            },
            Tab.Timetable to { _ ->
                if (canClick) {
                    if (tabHolders[Tab.Timetable]?.visibility == View.GONE) {
                        showProgress()
                        startTimetableRequest()
                    } else {
                        switchTab(Tab.Timetable)
                    }
                }
            },
            Tab.Messages to { _ ->
                if (canClick) {
                    if (tabHolders[Tab.Messages]?.visibility == View.GONE) {
                        refreshMessages(MessageDescriptor.SortType.SendDate)
                    } else {
                        switchTab(Tab.Messages)
                    }
                }
            },
            Tab.Tests to { _ ->
                if (canClick) {
                    if (tabHolders[Tab.Tests]?.visibility == View.GONE) {
                        showProgress()
                        controller.getTestList()
                    } else {
                        switchTab(Tab.Tests)
                    }
                }
            },
            Tab.Noticeboard to { _ ->
                if (canClick) {
                    if (tabHolders[Tab.Noticeboard]?.visibility == View.GONE) {
                        showProgress()
                        controller.getNoticeList()
                    } else {
                        switchTab(Tab.Noticeboard)
                    }
                }
            }
        )
        tabSortSpinners = mutableMapOf(
            Tab.Notes to notes_spinner,
            Tab.Absences to abs_spinner,
            Tab.Messages to messages_spinner,
            Tab.Evaluations to evals_spinner,
            Tab.Homework to homework_spinner,
            Tab.Noticeboard to noticeboard_spinner
        )
    }
    private fun setupClickListeners() {
        canClick = true
        name_tt.setOnClickListener {
            if (canClick) {
                if (details_ll.visibility == View.GONE) {
                    controller.getStudentDetails()
                }
                hideDetails()
            }
        }
        for (button in tabButtons) {
            button.value.setOnClickListener(tabOnClickListeners[button.key])
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
            Tab.Noticeboard -> {
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val sortType = Notice.sortTypeFromString(spinnerPair.value.selectedItem.toString())
                        refreshNotices(sortType)
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
        val ta = this.obtainStyledAttributes(intArrayOf(R.attr.spinnerItemLayout, R.attr.spinnerDropdownItemLayout))
        val spinnerDisplayArrayMap = mapOf(
            Tab.Notes to listOf("Date", "Type", "Teacher"),
            Tab.Absences to listOf("Subject", "Teacher", "Lesson start time", "Creating time", "Justification state"),
            Tab.Messages to listOf("Send date", "Teacher"),
            Tab.Evaluations to listOf("Creating time", "Form", "Value", "Mode", "Subject", "Teacher"),
            Tab.Homework to listOf("Post date", "Deadline", "Teacher", "Subject"),
            Tab.Noticeboard to listOf("Valid from", "Valid until", "Title")
        )
        val spinnerDisplayList = spinnerDisplayArrayMap[spinnerPair.key]
        if (spinnerDisplayList != null) {
            val adapter =
                ArrayAdapter(this, ta.getResourceId(0, 0), spinnerDisplayList)
            adapter.setDropDownViewResource(ta.getResourceId(1, 0))
            spinnerPair.value.adapter = adapter
        }
        ta.recycle()
    }
    private fun setupSpinners() {
        for (spinnerPair in tabSortSpinners) {
            setupSpinnerAdapter(spinnerPair)
            setupItemSelectedListener(spinnerPair)
        }
    }

    private fun setupTabHolders() {
        for (holder in tabHolders) {
            holder.value.visibility = View.GONE
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
        UIHelper.displayError(this, main_cl, error)
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
        Tests,
        Noticeboard
    }
    private fun closeTabs(exception: Tab? = null) {
        for (tabHolder in tabHolders) {
            if (tabHolder.key != exception) {
                tabHolder.value.visibility = View.GONE
                tabSortSpinners[tabHolder.key]?.visibility = View.GONE
                tabButtons[tabHolder.key]?.setBackgroundColor(getColorFromAttr(R.attr.colorButtonUnselected))
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
                tabButton.setBackgroundColor(getColorFromAttr(R.attr.colorButtonSelected))
            } else {
                if (canClose) {
                    newVisibility = View.GONE
                    tabButton.setBackgroundColor(getColorFromAttr(R.attr.colorButtonUnselected))
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
            tabHolders[Tab.Timetable], details_ll, ::showDetails, ::hideDetails, controller, ::getColorFromAttr)
        switchTab(Tab.Timetable)
        hideProgress()
    }
    override fun generateMessageDescriptors(messages: List<MessageDescriptor>) {
        MessageUI.generateMessageDescriptors(this, messages, tabHolders[Tab.Messages], controller, ::getColorFromAttr)
        switchTab(Tab.Messages, false)
        hideProgress()
    }
    override fun generateMessage(message: MessageDescriptor) {
        MessageUI.generateMessage(this, message.message, details_ll, ::downloadAttachment, ::showDetails, ::hideDetails, ::getColorFromAttr)
    }

    override fun generateTestList(tests: List<Test>) {
        tabHolders[Tab.Tests]?.removeAllViews()
        TestUI.generateTests(this, tests, tabHolders[Tab.Tests], details_ll, ::showDetails, ::hideDetails)
        switchTab(Tab.Tests)
        hideProgress()
    }

    override fun generateNoteList(notes: List<Note>) {
        this.notes = notes
        refreshNotes()
        switchTab(Tab.Notes)
        hideProgress()
    }

    override fun generateNoticeList(notices: List<Notice>) {
        this.notices = notices
        refreshNotices()
        switchTab(Tab.Noticeboard)
        hideProgress()
    }

    override fun generateHomeworkList(homeworks: List<Homework>) {
        this.homeworks = homeworks
        refreshHomeworks()
        switchTab(Tab.Homework)
        hideProgress()
    }

    override fun generateHomeworkCommentList(homeworkComments: List<HomeworkComment>) {
        homeworkCommentHolder = HomeworkUI.generateHomeworkCommentList(this, homeworkComments, details_ll, homeworkCommentHolder)
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
            val themeToggleButton = UIHelper.generateButton(this, "TOGGLE THEME", ::toggleTheme, ::showDetails, ::hideDetails, details_ll)
            themeToggleButton.setBackgroundColor(getColorFromAttr(R.attr.colorAccent))
            details_ll.addView(themeToggleButton)
            details_ll.addView(nameDetailsTextView)
            showDetails()
        }
    }

    private fun refreshHomeworks(sortType: Homework.SortType = Homework.SortType.PostDate) {
        val holder = tabHolders[Tab.Homework]
        holder?.removeAllViews()
        HomeworkUI.generateHomeworkList(this, homeworks.sortedWith(compareBy(sortType.lambda)), tabHolders[Tab.Homework], details_ll, ::showDetails, ::hideDetails,
            ::triggerGetHomeworkCommentList, ::sendHomeworkComment, R.attr.selectedButtonStyle, ::getColorFromAttr)
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
        controller.getMessageList(sortType)
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
    private fun refreshNotices(sortType: Notice.SortType = Notice.SortType.ValidFrom) {
        val holder = tabHolders[Tab.Noticeboard]
        holder?.removeAllViews()
        NoticeUI.generateNoticeList(
            this, notices.sortedWith(compareBy(sortType.lambda)),
            holder, details_ll, ::showDetails, ::hideDetails
        )
    }

    private fun triggerGetHomeworkCommentList(homeworkUid: String) {
        controller.getHomeworkCommentList(homeworkUid)
    }
    private fun sendHomeworkComment(homeworkUid: String, text: String) {
        controller.sendHomeworkComment(homeworkUid, text)
    }
    override fun refreshToken(tokens: Map<String, String>) {
        val sharedPref = getSharedPreferences(getString(R.string.auth_path), Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString("accessToken", tokens["access_token"])
            putString("refreshToken", tokens["refresh_token"])
            commit()
        }
    }

    private fun downloadAttachment(attachment: Attachment) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        runWithPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            controller.downloadAttachment(downloadManager, attachment)
        }
    }

    override fun sendToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    private fun toggleTheme(view: View): List<View> {
        val sharedPref = getSharedPreferences(getString(R.string.theme_path), Context.MODE_PRIVATE) ?: return listOf()
        with (sharedPref.edit()) {
            putBoolean("dark", !sharedPref.getBoolean("dark", true))
            commit()
        }
        recreate()
        return listOf()
    }

    override fun refreshCommentList(homeworkUid: String) {
        controller.getHomeworkCommentList(homeworkUid)
    }

    private fun startTimetableRequest() {
        val firstDay = LocalDateTime.now().with(DayOfWeek.MONDAY)
        val startDate = KretaDate(firstDay)
        val endDate = KretaDate(firstDay.plusDays(6))
        controller.getTimetable(startDate, endDate)
    }
}
