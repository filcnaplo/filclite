package hu.filcnaplo.ellenorzo.lite.activity

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerFuture
import android.app.ActionBar
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import hu.filcnaplo.ellenorzo.lite.R
import hu.filcnaplo.ellenorzo.lite.controller.MainController
import hu.filcnaplo.ellenorzo.lite.kreta.KretaDate
import hu.filcnaplo.ellenorzo.lite.kreta.StudentDetails
import hu.filcnaplo.ellenorzo.lite.kreta.data.homework.Homework
import hu.filcnaplo.ellenorzo.lite.kreta.data.homework.HomeworkComment
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.Attachment
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.LongerMessageDescriptor
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.MessageDescriptor
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Absence
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Evaluation
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Note
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Notice
import hu.filcnaplo.ellenorzo.lite.kreta.data.timetable.SchoolClass
import hu.filcnaplo.ellenorzo.lite.kreta.data.timetable.SchoolDay
import hu.filcnaplo.ellenorzo.lite.kreta.data.timetable.Test
import hu.filcnaplo.ellenorzo.lite.ui.*
import hu.filcnaplo.ellenorzo.lite.ui.manager.RefreshableData
import hu.filcnaplo.ellenorzo.lite.ui.manager.UIManager
import hu.filcnaplo.ellenorzo.lite.view.MainView
import kotlinx.android.synthetic.main.activity_main.*
import java.time.DayOfWeek
import java.time.LocalDateTime


class MainActivity : AppCompatActivity(), MainView {
    private lateinit var controller: MainController
    private lateinit var themeHelper: ThemeHelper

    private var managers = mutableMapOf<Tab, UIManager>()

    private var canClick = true

    private lateinit var homeworkCommentHolder: LinearLayout
    private var messageType = MessageDescriptor.Type.Inbox

    private var account: Account? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeHelper = ThemeHelper(this)
        themeHelper.setCurrentTheme()
        setContentView(R.layout.activity_main)
        val accountManager = AccountManager.get(this)
        val future: AccountManagerFuture<Bundle> =
            accountManager.getAuthTokenByFeatures(getString(R.string.kreta_account_id), getString(R.string.kreta_auth_type_full),
                null,
                this,
                null,
                null,
                { future ->
                    val bnd = future.result
                    val accessToken = bnd.getString(AccountManager.KEY_AUTHTOKEN)
                    account = Account(bnd.getString(AccountManager.KEY_ACCOUNT_NAME), bnd.getString(AccountManager.KEY_ACCOUNT_TYPE))
                    val refreshToken = accountManager.getUserData(account, getString(R.string.key_refresh_token))
                    val instituteCode = accountManager.getUserData(account, getString(R.string.key_institute_code))
                    if (accessToken != null && refreshToken != null && instituteCode != null) {
                        controller = MainController(this, this, accessToken, refreshToken, instituteCode)
                        initializeActivity()
                    } else {
                        sendToLogin()
                    }
                },
                null)
    }

    private fun initializeActivity() {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerDefaultNetworkCallback(controller)
        setupManagers()
        name_tt.setOnClickListener {
            if (canClick) {
                if (details_ll.visibility == View.GONE) {
                    generateSettings()
                } else {
                    toggleDetails(true)
                }
            }
        }

        homeworkCommentHolder = LinearLayout(this)
        controller.getStudentDetails()
    }

    private fun setupManagers() {
        val tabHolders = mutableMapOf(
            Tab.Evaluations to eval_holder_ll,
            Tab.Notes to note_holder_ll,
            Tab.Absences to abs_holder_ll,
            Tab.Homework to homework_holder_ll,
            Tab.Timetable to timetable_holder_ll,
            Tab.Messages to messages_holder_ll,
            Tab.Tests to tests_holder_ll,
            Tab.Noticeboard to noticeboard_holder_ll
        )
        val tabButtons = mutableMapOf(
            Tab.Evaluations to evals_btt,
            Tab.Notes to notes_btt,
            Tab.Absences to abs_btt,
            Tab.Homework to homework_btt,
            Tab.Timetable to timetable_btt,
            Tab.Messages to messages_btt,
            Tab.Tests to tests_btt,
            Tab.Noticeboard to noticeboard_btt
        )
        val tabOnEnterListeners = mutableMapOf(
            Tab.Absences to {
                showProgress()
                controller.getAbsenceList()
            },
            Tab.Evaluations to {
                showProgress()
                controller.getEvaluationList()
            },
            Tab.Notes to {
                showProgress()
                controller.getNoteList()
            },
            Tab.Homework to {
                showProgress()
                val firstDay = LocalDateTime.now().with(DayOfWeek.MONDAY)
                val startDate = KretaDate(firstDay)
                controller.getHomeworkList(startDate)
            },
            Tab.Timetable to {
                showProgress()
                val firstDay = LocalDateTime.now().with(DayOfWeek.MONDAY)
                val startDate = KretaDate(firstDay)
                val endDate = KretaDate(firstDay.plusDays(6))
                controller.getTimetable(startDate, endDate)
            },
            Tab.Messages to {
                showProgress()
                controller.getMessageList(MessageDescriptor.Type.Inbox)
            },
            Tab.Tests to {
                showProgress()
                controller.getTestList()
            },
            Tab.Noticeboard to {
                showProgress()
                controller.getNoticeList()
            }
        )
        val tabOnExitListeners = mutableMapOf(
            Tab.Absences to {
                switchTab(Tab.Absences)
            },
            Tab.Evaluations to {
                switchTab(Tab.Evaluations)
            },
            Tab.Notes to {
                switchTab(Tab.Notes)
            },
            Tab.Homework to {
                switchTab(Tab.Homework)
            },
            Tab.Timetable to {
                switchTab(Tab.Timetable)
            },
            Tab.Messages to {
                switchTab(Tab.Messages)
            },
            Tab.Tests to {
                switchTab(Tab.Tests)
            },
            Tab.Noticeboard to {
                switchTab(Tab.Noticeboard)
            }
        )
        val tabOnElemClickListener = mutableMapOf<Tab, (View, RefreshableData) -> List<View>>(
            Tab.Absences to { _: View, elem: RefreshableData ->
                val absDetailsTextView = TextView(this)
                absDetailsTextView.text = elem.absence?.toDetailedString()
                listOf(absDetailsTextView)
            },
            Tab.Evaluations to { _: View, elem: RefreshableData ->
                val evalDetailsTextView = TextView(this)
                evalDetailsTextView.text = elem.eval?.toDetailedString()
                listOf(evalDetailsTextView)
            },
            Tab.Homework to { _: View, elem: RefreshableData ->
                if (elem.homework != null) {
                    HomeworkUI.generateHomework(this, elem.homework, themeHelper, ::sendHomeworkComment, ::triggerGetHomeworkCommentList)
                } else {
                    listOf()
                }
            },
            Tab.Messages to { _: View, elem: RefreshableData ->
                controller.getMessage(elem.messageDescriptor?.id ?: 0)
                controller.setMessageRead(elem.messageDescriptor?.id ?: 0)
                listOf<View>()
            },
            Tab.Notes to { _: View, elem: RefreshableData ->
                val noteDetailsTextView = TextView(this)
                noteDetailsTextView.text = elem.note?.toDetailedString()
                Linkify.addLinks(noteDetailsTextView, Linkify.EMAIL_ADDRESSES + Linkify.WEB_URLS)
                listOf(noteDetailsTextView)
            },
            Tab.Noticeboard to { _: View, elem: RefreshableData ->
                val noticeDetailsTextView = TextView(this)
                noticeDetailsTextView.text = elem.notice?.toDetailedString()
                Linkify.addLinks(noticeDetailsTextView, Linkify.EMAIL_ADDRESSES + Linkify.WEB_URLS)
                listOf(noticeDetailsTextView)
            },
            Tab.Tests to { _: View, elem: RefreshableData ->
                val testDetailsTextView = TextView(this)
                testDetailsTextView.text = elem.test?.toDetailedString()
                listOf(testDetailsTextView)
            }
        )
        val tabOnItemSelectedListener = mutableMapOf(
            Tab.Absences to {
                parent: AdapterView<*>?,
                view: View?,
                _: Int,
                _: Long ->
                val manager = managers[Tab.Absences]
                if (manager != null) {
                    val stringToSortType = mapOf(
                        getString(R.string.subject_ma) to Absence.SortType.Subject,
                        getString(R.string.teacher_ma) to Absence.SortType.Teacher,
                        getString(R.string.lesson_start_time_ma) to Absence.SortType.ClassStartDate,
                        getString(R.string.creating_time_ma) to Absence.SortType.Date,
                        getString(R.string.justification_state_ma) to Absence.SortType.JustificationState)
                    manager.sortType = SortType(stringToSortType[parent?.selectedItem.toString()] ?: Absence.SortType.Subject)
                    if (!manager.firstSpinnerSelection) {
                        controller.getAbsenceList()
                    } else {
                        manager.firstSpinnerSelection = false
                    }
                }
            },
            Tab.Notes to {
                parent: AdapterView<*>?,
                _: View?,
                _: Int,
                _: Long ->
                val manager = managers[Tab.Notes]
                if (manager != null) {
                    val stringToSortType = mapOf(
                        getString(R.string.date_ma) to Note.SortType.Date,
                        getString(R.string.type_ma) to Note.SortType.Type,
                        getString(R.string.teacher_ma) to Note.SortType.Teacher)
                    manager.sortType = SortType(stringToSortType[parent?.selectedItem.toString()] ?: Note.SortType.Date)
                    if (!manager.firstSpinnerSelection) {
                        controller.getNoteList()
                    } else {
                        manager.firstSpinnerSelection = false
                    }
                }
            },
            Tab.Noticeboard to {
                    parent: AdapterView<*>?,
                    _: View?,
                    _: Int,
                    _: Long ->
                val manager = managers[Tab.Noticeboard]
                if (manager != null) {
                    val stringToSortType = mapOf(
                        getString(R.string.valid_from_ma) to Notice.SortType.ValidFrom,
                        getString(R.string.valid_until_ma) to Notice.SortType.ValidUntil,
                        getString(R.string.title_ma) to Notice.SortType.Title)
                    manager.sortType = SortType(stringToSortType[parent?.selectedItem.toString()] ?: Notice.SortType.ValidFrom)
                    if (!manager.firstSpinnerSelection) {
                        controller.getNoticeList()
                    } else {
                        manager.firstSpinnerSelection = false
                    }
                }
            },
            Tab.Messages to {
                    parent: AdapterView<*>?,
                    _: View?,
                    _: Int,
                    _: Long ->
                val manager = managers[Tab.Messages]
                if (manager != null) {
                    val stringToSortType = mapOf(
                        getString(R.string.send_date_ma) to MessageDescriptor.SortType.SendDate,
                        getString(R.string.teacher_ma) to MessageDescriptor.SortType.Teacher)
                    manager.sortType = SortType(stringToSortType[parent?.selectedItem.toString()] ?: MessageDescriptor.SortType.SendDate)
                    if (!manager.firstSpinnerSelection) {
                        controller.getMessageList(messageType)
                    } else {
                        manager.firstSpinnerSelection = false
                    }
                }
            },
            Tab.Evaluations to {
                    parent: AdapterView<*>?,
                    _: View?,
                    _: Int,
                    _: Long ->
                val manager = managers[Tab.Evaluations]
                if (manager != null) {
                    val stringToSortType = mapOf(
                        getString(R.string.creating_time_ma) to Evaluation.SortType.CreatingDate,
                        getString(R.string.form_ma) to Evaluation.SortType.Nature,
                        getString(R.string.value_ma) to Evaluation.SortType.TextValue,
                        getString(R.string.mode_ma) to Evaluation.SortType.Mode,
                        getString(R.string.subject_ma) to Evaluation.SortType.Subject,
                        getString(R.string.teacher_ma) to Evaluation.SortType.Teacher)
                    manager.sortType = SortType(stringToSortType[parent?.selectedItem.toString()] ?: Evaluation.SortType.CreatingDate)
                    if (!manager.firstSpinnerSelection) {
                        controller.getEvaluationList()
                    } else {
                        manager.firstSpinnerSelection = false
                    }
                }
            },
            Tab.Homework to {
                    parent: AdapterView<*>?,
                    _: View?,
                    _: Int,
                    _: Long ->
                val manager = managers[Tab.Homework]
                if (manager != null) {
                    val stringToSortType = mapOf(
                        getString(R.string.post_date_ma) to Homework.SortType.PostDate,
                        getString(R.string.deadline_ma) to Homework.SortType.Deadline,
                        getString(R.string.teacher_ma) to Homework.SortType.Teacher,
                        getString(R.string.subject_ma) to Homework.SortType.Subject
                    )
                    manager.sortType = SortType(stringToSortType[parent?.selectedItem.toString()] ?: Homework.SortType.PostDate)
                    if (!manager.firstSpinnerSelection) {
                        val firstDay = LocalDateTime.now().with(DayOfWeek.MONDAY)
                        val startDate = KretaDate(firstDay)
                        controller.getHomeworkList(startDate)
                    } else {
                        manager.firstSpinnerSelection = false
                    }
                }
            }
        )
        val tabSortSpinners = mutableMapOf(
            Tab.Notes to notes_spinner,
            Tab.Absences to abs_spinner,
            Tab.Messages to messages_spinner,
            Tab.Evaluations to evals_spinner,
            Tab.Homework to homework_spinner,
            Tab.Noticeboard to noticeboard_spinner
        )
        val tabSortType = mutableMapOf(
            Tab.Notes to SortType(Note.SortType.Date),
            Tab.Absences to SortType(Absence.SortType.Subject),
            Tab.Messages to SortType(MessageDescriptor.SortType.SendDate),
            Tab.Evaluations to SortType(Evaluation.SortType.CreatingDate),
            Tab.Homework to SortType(Homework.SortType.PostDate),
            Tab.Noticeboard to SortType(Notice.SortType.ValidFrom)
        )
        val canClick = {canClick}
        val tabSpinnerElements = mapOf(
            Tab.Notes to listOf(getString(R.string.date_ma),
                getString(R.string.type_ma),
                getString(R.string.teacher_ma)),
            Tab.Absences to listOf(getString(R.string.subject_ma),
                getString(R.string.teacher_ma),
                getString(R.string.lesson_start_time_ma),
                getString(R.string.creating_time_ma),
                getString(R.string.justification_state_ma)),
            Tab.Messages to listOf(getString(R.string.send_date_ma),
                getString(R.string.teacher_ma)),
            Tab.Evaluations to listOf(getString(R.string.creating_time_ma),
                getString(R.string.form_ma),
                getString(R.string.value_ma),
                getString(R.string.mode_ma),
                getString(R.string.subject_ma),
                getString(R.string.teacher_ma)),
            Tab.Homework to listOf(getString(R.string.post_date_ma),
                getString(R.string.deadline_ma),
                getString(R.string.teacher_ma),
                getString(R.string.subject_ma)),
            Tab.Noticeboard to listOf(getString(R.string.valid_from_ma),
                getString(R.string.valid_until_ma),
                getString(R.string.title_ma))
        )
        val tabGetElemColor = mapOf<Tab, (RefreshableData) -> Int>()
        val tabGetElemTextColor = mapOf(
            Tab.Absences to { elem: RefreshableData ->
                getColor(R.color.darkColorText)
                if (elem.absence?.justificationState == "Igazolt") {
                    getColor(R.color.colorAbsJustified)
                } else {
                    getColor(R.color.colorAbsUnjustified)
                }
            },
            Tab.Evaluations to { elem: RefreshableData ->
                when (elem.eval?.numberValue) {
                    1 -> getColor(R.color.colorOne)
                    2 -> getColor(R.color.colorTwo)
                    3 -> getColor(R.color.colorThree)
                    4 -> getColor(R.color.colorFour)
                    5 -> getColor(R.color.colorFive)
                    else -> themeHelper.getColorFromAttributes(R.attr.colorText)
                }
            },
            Tab.Homework to { elem: RefreshableData ->
                val homework = elem.homework
                if (homework != null) {
                    getColor(HomeworkUI.getColorFromDeadline(homework))
                } else {
                    themeHelper.getColorFromAttributes(R.attr.colorText)
                }
            },
            Tab.Messages to { elem: RefreshableData ->
                if (elem.messageDescriptor?.isRead == true) {
                    themeHelper.getColorFromAttributes(R.attr.colorUnavailable)
                } else {
                    themeHelper.getColorFromAttributes(R.attr.colorText)
                }
            }
        )
        val tabOnRefreshListeners = mapOf(
            Tab.Messages to {
                val manager = managers[Tab.Messages]
                val categoryHolder = LinearLayout(this)
                categoryHolder.orientation = LinearLayout.HORIZONTAL
                categoryHolder.gravity = Gravity.CENTER
                val categoryButtons = mutableListOf<Button>()
                val params = ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                val setSelectedCategory = { category: Int ->
                    for (button in categoryButtons) {
                        button.setBackgroundColor(themeHelper.getColorFromAttributes(R.attr.colorButtonUnselected))
                    }
                    categoryButtons[category].setBackgroundColor(themeHelper.getColorFromAttributes(R.attr.colorButtonSelected))
                }
                val inbox = UIHelper.generateButton(this, getString(R.string.inbox_ma), { _: View, _: RefreshableData ->
                    messageType = MessageDescriptor.Type.Inbox
                    controller.getMessageList(messageType)
                    setSelectedCategory(0)
                    null
                })
                inbox.layoutParams = params
                categoryButtons.add(inbox)
                val sent = UIHelper.generateButton(this, getString(R.string.sent_ma), { _: View, _: RefreshableData ->
                    messageType = MessageDescriptor.Type.Sent
                    controller.getMessageList(messageType)
                    setSelectedCategory(1)
                    null
                })
                sent.layoutParams = params
                categoryButtons.add(sent)
                val trash = UIHelper.generateButton(this, getString(R.string.trash_ma), { _: View, _: RefreshableData ->
                    messageType = MessageDescriptor.Type.Trash
                    controller.getMessageList(messageType)
                    setSelectedCategory(2)
                    null
                })
                trash.layoutParams = params
                categoryButtons.add(trash)
                for (button in categoryButtons) {
                    categoryHolder.addView(button)
                }
                val index = when(messageType) {
                    MessageDescriptor.Type.Inbox -> 0
                    MessageDescriptor.Type.Sent -> 1
                    MessageDescriptor.Type.Trash -> 2
                }
                setSelectedCategory(index)
                manager?.holder?.addView(categoryHolder)
            }
        )
        val tabCustomViews = mapOf(
            Tab.Evaluations to listOf(evals_type_spinner)
        )
        for (tab in Tab.values()) {
            val uiManager = UIManager(this,
                themeHelper,
                tabHolders[tab] ?: LinearLayout(this),
                tabButtons[tab] ?: Button(this),
                tabGetElemColor[tab] ?: {themeHelper.getColorFromAttributes(R.attr.colorButtonUnselected)},
                tabGetElemTextColor[tab] ?: {themeHelper.getColorFromAttributes(R.attr.colorText)},
                canClick,
                tabOnEnterListeners[tab] ?: {},
                tabOnExitListeners[tab] ?: {},
                tabOnRefreshListeners[tab] ?: {} ,
                tabOnElemClickListener[tab] ?: {_: View, _: RefreshableData -> listOf()},
                ::toggleDetails,
                details_ll,
                tabSortSpinners[tab],
                tabSortType[tab],
                tabSpinnerElements[tab],
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        tabOnItemSelectedListener[tab]?.invoke(parent, view, position, id)
                    }
                },
                tabCustomViews[tab]
            )
            managers[tab] = uiManager
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
    override fun displaySuccess(success: String) {
        UIHelper.displaySuccess(this, main_cl, success)
    }

    private fun toggleDetails(hide: Boolean = false) {
        details_ll.visibility = if (details_ll.visibility == View.VISIBLE || hide)
        {
            details_ll.removeAllViews()
            View.GONE
        } else {
            scroll_view.smoothScrollTo(0, 0)
            View.VISIBLE
        }
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
        for (managerPair in managers) {
            if (managerPair.key != exception) {
                managerPair.value.setVisibility(View.GONE)
            }
        }
        toggleDetails(true)
    }
    private fun switchTab(newTab: Tab, canClose: Boolean = true) {
        closeTabs(newTab)
        val manager = managers[newTab]
        if (manager != null) {
            val newVisibility = if(manager.holder.visibility == View.GONE) {View.VISIBLE} else {if (canClose) {View.GONE} else {View.VISIBLE}}
            manager.setVisibility(newVisibility)
        }
    }

    override fun generateTimetable(timetable: Map<SchoolDay, List<SchoolClass>>) {
        TimetableUI.generateTimetable(this, timetable,
            managers[Tab.Timetable]?.holder, details_ll, ::toggleDetails, controller, themeHelper)
        switchTab(Tab.Timetable)
        hideProgress()
    }
    override fun generateMessageDescriptors(messages: List<MessageDescriptor>) {
        val tab = Tab.Messages
        val manager = managers[tab]
        if (manager != null) {
            val elems = mutableListOf<RefreshableData>()
            for (message in messages.sortedWith(compareBy(manager.sortType?.messageDescriptor?.lambda ?: MessageDescriptor.SortType.SendDate.lambda))) {
                elems.add(RefreshableData(message.toString(), null, null, null, message))
            }
            managers[tab]?.refresh(elems)
        }
        switchTab(tab, false)
        hideProgress()
    }
    override fun generateMessage(message: LongerMessageDescriptor) {
        MessageUI.generateMessage(this, message, details_ll, ::downloadAttachment, ::toggleDetails, themeHelper, ::trashMessage)
    }

    override fun generateTestList(tests: List<Test>) {
        val tab = Tab.Tests
        val manager = managers[tab]
        if (manager != null) {
            val elems = mutableListOf<RefreshableData>()
            for (test in tests) {
                elems.add(RefreshableData(test.toString(), null, null, null, null, null, null, test))
            }
            managers[tab]?.refresh(elems)
        }
        switchTab(tab)
        hideProgress()
    }

    override fun generateNoteList(notes: List<Note>) {
        val tab = Tab.Notes
        val manager = managers[tab]
        if (manager != null) {
            val elems = mutableListOf<RefreshableData>()
            for (note in notes.sortedWith(compareBy(manager.sortType?.note?.lambda ?: Note.SortType.Date.lambda))) {
                elems.add(RefreshableData(note.toString(), null, null, null, null, note))
            }
            managers[tab]?.refresh(elems)
        }
        switchTab(tab, false)
        hideProgress()
    }

    override fun generateNoticeList(notices: List<Notice>) {
        val tab = Tab.Noticeboard
        val manager = managers[tab]
        if (manager != null) {
            val elems = mutableListOf<RefreshableData>()
            for (notice in notices.sortedWith(compareBy(manager.sortType?.notice?.lambda ?: Notice.SortType.ValidFrom.lambda))) {
                elems.add(RefreshableData(notice.toString(), null, null, null, null, null, notice))
            }
            managers[tab]?.refresh(elems)
        }
        switchTab(tab, false)
        hideProgress()
    }

    override fun generateHomeworkList(homeworks: List<Homework>) {
        val tab = Tab.Homework
        val manager = managers[tab]
        if (manager != null) {
            val elems = mutableListOf<RefreshableData>()
            for (homework in homeworks.sortedWith(compareBy(manager.sortType?.homework?.lambda ?: Homework.SortType.PostDate.lambda))) {
                elems.add(RefreshableData(homework.toString(), null, null, homework))
            }
            managers[tab]?.refresh(elems)
        }
        switchTab(tab, false)
        hideProgress()
    }

    override fun generateHomeworkCommentList(homeworkComments: List<HomeworkComment>) {
        homeworkCommentHolder = HomeworkUI.generateHomeworkCommentList(this, homeworkComments, details_ll, homeworkCommentHolder)
    }

    override fun generateEvaluationList(evaluations: List<Evaluation>) {
        val tab = Tab.Evaluations
        val manager = managers[tab]
        if (manager != null) {
            val stringTypes = mutableListOf<String>()
            for (eval in evaluations) {
                if (!stringTypes.contains(eval.type?.description))
                    stringTypes.add(eval.type?.description ?: "Unknown type") //TODO: Translation
            }
            val spinnerLayouts = themeHelper.getResourcesFromAttributes(
                listOf(
                    R.attr.sortSpinnerItemLayout,
                    R.attr.sortSpinnerDropdownItemLayout
                )
            )
            val adapter =
                ArrayAdapter(this, spinnerLayouts[0], stringTypes)
            adapter.setDropDownViewResource(spinnerLayouts[1])
            val currentSelection = evals_type_spinner.selectedItem?.toString()
            var usedLastSelection = false
            evals_type_spinner.adapter = adapter
            if (stringTypes.contains(currentSelection)) {
                evals_type_spinner.setSelection(adapter.getPosition(currentSelection))
                usedLastSelection = true
            }
            evals_type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    if (!usedLastSelection)
                        controller.getEvaluationList()
                    usedLastSelection = false
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            val elems = mutableListOf<RefreshableData>()
            for (eval in evaluations.sortedWith(compareBy(manager.sortType?.eval?.lambda ?: Evaluation.SortType.CreatingDate.lambda))) {
                if (eval.type?.description == evals_type_spinner.selectedItem) {
                    elems.add(RefreshableData(eval.toString(), null, eval))
                }
            }
            managers[tab]?.refresh(elems)
        }
        switchTab(tab, false)
        hideProgress()
    }

    override fun generateAbsenceList(absences: List<Absence>) {
        val tab = Tab.Absences
        val manager = managers[tab]
        if (manager != null) {
            val elems = mutableListOf<RefreshableData>()
            for (abs in absences.sortedWith(compareBy(manager.sortType?.abs?.lambda ?: Absence.SortType.Subject.lambda))) {
                elems.add(RefreshableData(abs.toString(), abs))
            }
            managers[tab]?.refresh(elems)
        }
        switchTab(tab, false)
        hideProgress()
    }
    
    private fun generateSettings() {
        val themeToggleButton = UIHelper.generateButton(this, getString(R.string.toggle_theme_ma), {_, _ -> toggleTheme()}, null, ::toggleDetails, details_ll)
        val studentDetailsButton = UIHelper.generateButton(this, getString(R.string.student_details_ma), {_, _ -> controller.getStudentDetails(); listOf()}, null, ::toggleDetails, details_ll)
        val addAccountButton = UIHelper.generateButton(this, getString(R.string.add_account_ma), {_, _ -> sendToLogin(); listOf()}, null, ::toggleDetails, details_ll)
        val buttons = listOf(themeToggleButton, studentDetailsButton, addAccountButton)
        for (button in buttons) {
            button.setBackgroundColor(themeHelper.getColorFromAttributes(R.attr.colorAccent))
            details_ll.addView(button)
        }
        toggleDetails()
    }

    override fun generateStudentDetails(studentDetails: StudentDetails) {
        val studentName = studentDetails.toString()
        if (name_tt.text != studentName) {
            name_tt.text = studentName
        } else {
            val nameDetailsTextView = TextView(this)
            Log.w("student", "yea")
            nameDetailsTextView.text = studentDetails.toDetailedString()
            details_ll.addView(nameDetailsTextView)
        }
    }

    private fun triggerGetHomeworkCommentList(homeworkUid: String) {
        controller.getHomeworkCommentList(homeworkUid)
    }
    private fun sendHomeworkComment(homeworkUid: String, text: String) {
        controller.sendHomeworkComment(homeworkUid, text)
    }
    override fun refreshToken(tokens: Map<String, String>) {
        val accountManager = AccountManager.get(this)
        accountManager.setAuthToken(account, getString(R.string.kreta_auth_type_full), tokens["access_token"])
        accountManager.setUserData(account, getString(R.string.key_refresh_token), tokens["refresh_token"])
    }

    private fun downloadAttachment(attachment: Attachment) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        runWithPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            controller.downloadAttachment(downloadManager, attachment)
        }
    }
    private fun trashMessage(messageId: Int, isTrashed: Boolean) {
        controller.trashMessage(messageId, isTrashed)
    }

    override fun sendToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    private fun toggleTheme(): List<View> {
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
}
