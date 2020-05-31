package com.thegergo02.minkreta.controller

import android.app.DownloadManager
import android.content.Context
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.kreta.data.timetable.Test
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.kreta.KretaError
import com.thegergo02.minkreta.kreta.KretaRequests
import com.thegergo02.minkreta.kreta.StudentDetails
import com.thegergo02.minkreta.kreta.data.homework.Homework
import com.thegergo02.minkreta.kreta.data.homework.HomeworkComment
import com.thegergo02.minkreta.kreta.data.message.Attachment
import com.thegergo02.minkreta.kreta.data.sub.Absence
import com.thegergo02.minkreta.kreta.data.sub.Evaluation
import com.thegergo02.minkreta.kreta.data.sub.Note
import com.thegergo02.minkreta.kreta.data.sub.Notice
import com.thegergo02.minkreta.view.MainView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainController(ctx: Context, private var mainView: MainView?, accessToken: String, refreshToken: String, instituteCode: String)
    :
    KretaRequests.OnEvaluationListResult,
    KretaRequests.OnRefreshTokensResult,
    KretaRequests.OnTimetableResult,
    KretaRequests.OnMessageListResult,
    KretaRequests.OnMessageResult,
    KretaRequests.OnTestListResult,
    KretaRequests.OnHomeworkListResult,
    KretaRequests.OnStudentDetailsResult,
    KretaRequests.OnNoteListResult,
    KretaRequests.OnAbsenceListResult,
    KretaRequests.OnHomeworkCommentListResult,
    KretaRequests.OnSendHomeworkCommentResult,
    KretaRequests.OnNoticeListResult
{

    private val apiHandler = KretaRequests(ctx, this, accessToken, refreshToken, instituteCode)

    fun getEvaluationList() {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getEvaluationList(parentListener)
        }
    }

    fun getTimetable(fromDate: KretaDate, toDate: KretaDate) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTimetable(parentListener, fromDate, toDate)
        }
    }

    fun getMessageList(type: MessageDescriptor.Type, sortType: MessageDescriptor.SortType) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getMessageList(parentListener, type, sortType)
        }
    }
    fun getMessage(messageId: Int) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getMessage(parentListener, messageId)
        }
    }
    fun downloadAttachment(downloadManager: DownloadManager, attachment: Attachment) {
        GlobalScope.launch {
            apiHandler.downloadAttachment(downloadManager, attachment)
        }
    }
    fun setMessageRead(messageId: Int, isRead: Boolean = true) {
        GlobalScope.launch {
            apiHandler.setMessageRead(messageId, isRead)
        }
    }

    fun sendHomeworkComment(homeworkUid: String, text: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.sendHomeworkComment(parentListener, homeworkUid, text)
        }
    }

    fun getTestList() {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTestList(parentListener)
        }
    }

    fun getNoteList() {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getNoteList(parentListener)
        }
    }

    fun getNoticeList() {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getNoticeList(parentListener)
        }
    }

    fun getHomeworkList(fromDate: KretaDate) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getHomeworkList(parentListener, fromDate)
        }
    }

    fun getHomeworkCommentList(homeworkUid: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getHomeworkCommentList(parentListener, homeworkUid)
        }
    }

    fun getAbsenceList() {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getAbsenceList(parentListener)
        }
    }

    fun getStudentDetails() {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getStudentDetails(parentListener)
        }
    }

    override fun onEvaluationListSuccess(evals: List<Evaluation>) {
        mainView?.generateEvaluationList(evals)
    }
    override fun onEvaluationListError(error: KretaError) {
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onTimetableSuccess(timetable: MutableMap<SchoolDay, MutableList<SchoolClass>>) {
        mainView?.generateTimetable(timetable)
    }
    override fun onTimetableError(error: KretaError) {
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onMessageListSuccess(messageList: List<MessageDescriptor>, sortType: MessageDescriptor.SortType) {
        mainView?.generateMessageDescriptors(messageList.reversed().sortedWith(compareBy(sortType.lambda)))
    }
    override fun onMessageListError(error: KretaError) {
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onMessageSuccess(message: MessageDescriptor) {
        mainView?.generateMessage(message)
    }
    override fun onMessageError(error: KretaError) {
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onRefreshTokensSuccess(tokens: Map<String, String>) {
        mainView?.refreshToken(tokens)
        mainView?.hideProgress()
    }
    override fun onRefreshTokensError(error: KretaError) {
        mainView?.sendToLogin()
    }

    override fun onTestListSuccess(testList: List<Test>) {
        mainView?.generateTestList(testList)
    }
    override fun onTestListError(error: KretaError) {
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onNoteListSuccess(notes: List<Note>) {
        mainView?.generateNoteList(notes)
    }
    override fun onNoteListError(error: KretaError) {
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onNoticeListSuccess(notices: List<Notice>) {
        mainView?.generateNoticeList(notices)
    }
    override fun onNoticeListError(error: KretaError) {
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onHomeworkListSuccess(homeworks: List<Homework>) {
        mainView?.generateHomeworkList(homeworks)
    }
    override fun onHomeworkListError(error: KretaError) {
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onHomeworkCommentListSuccess(homeworkComments: List<HomeworkComment>) {
        mainView?.generateHomeworkCommentList(homeworkComments)
    }
    override fun onHomeworkCommentListError(error: KretaError) {
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onAbsenceListSuccess(absences: List<Absence>) {
        mainView?.generateAbsenceList(absences)
    }
    override fun onAbsenceListError(error: KretaError) {
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onStudentDetailsSuccess(studentDetails: StudentDetails) {
        mainView?.generateStudentDetails(studentDetails)
    }
    override fun onStudentDetailsError(error: KretaError) {
        getStudentDetails()
    }

    override fun onSendHomeworkSuccess(homeworkUid: String) {
        mainView?.refreshCommentList(homeworkUid)
    }
    override fun onSendHomeworkError(error: KretaError) {
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }
}
