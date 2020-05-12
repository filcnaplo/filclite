package com.thegergo02.minkreta.controller

import android.app.DownloadManager
import com.android.volley.AuthFailureError
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.kreta.data.timetable.Test
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.kreta.KretaError
import com.thegergo02.minkreta.kreta.KretaRequests
import com.thegergo02.minkreta.kreta.StudentDetails
import com.thegergo02.minkreta.kreta.data.homework.Homework
import com.thegergo02.minkreta.kreta.data.message.Attachment
import com.thegergo02.minkreta.kreta.data.sub.Evaluation
import com.thegergo02.minkreta.view.MainView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainController(private var mainView: MainView?, private val apiHandler: KretaRequests)
    :
    KretaRequests.OnEvaluationListResult,
    KretaRequests.OnRefreshTokensResult,
    KretaRequests.OnTimetableResult,
    KretaRequests.OnMessageListResult,
    KretaRequests.OnMessageResult,
    KretaRequests.OnTestListResult,
    KretaRequests.OnHomeworkListResult,
    KretaRequests.OnStudentDetailsResult
{

    fun getEvaluationList(accessToken: String, instituteUrl: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getEvaluationList(parentListener, accessToken, instituteUrl)
        }
    }

    fun getTimetable(accessToken: String, instituteCode: String, fromDate: KretaDate, toDate: KretaDate) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTimetable(parentListener, accessToken, instituteCode, fromDate, toDate)
        }
    }

    fun getMessageList(accessToken: String, sortType: MessageDescriptor.SortType) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getMessageList(parentListener, accessToken, sortType)
        }
    }
    fun getMessage(accessToken: String, messageId: Int) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getMessage(parentListener, accessToken, messageId)
        }
    }
    fun downloadAttachment(accessToken: String, downloadManager: DownloadManager, attachment: Attachment) {
        GlobalScope.launch {
            apiHandler.downloadAttachment(accessToken, downloadManager, attachment)
        }
    }
    fun setMessageRead(accessToken: String, messageId: Int, isRead: Boolean = true) {
        GlobalScope.launch {
            apiHandler.setMessageRead(accessToken, messageId, isRead)
        }
    }

    fun refreshToken(refreshToken: String, instituteCode: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.refreshToken(parentListener, refreshToken, instituteCode)
        }
    }

    fun getTestList(accessToken: String, instituteUrl: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTestList(parentListener, accessToken, instituteUrl)
        }
    }

    fun getHomeworkList(accessToken: String, instituteUrl: String, fromDate: KretaDate) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getHomeworkList(parentListener, accessToken, instituteUrl, fromDate)
        }
    }

    fun getStudentDetails(accessToken: String, instituteUrl: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getStudentDetails(parentListener, accessToken, instituteUrl)
        }
    }

    override fun onEvaluationListSuccess(evals: List<Evaluation>) {
        mainView?.generateEvaluationList(evals)
    }
    override fun onEvaluationListError(error: KretaError) {
        when (error) {
            is KretaError.VolleyError -> {
                when (error.volleyError) {
                    is AuthFailureError -> {mainView?.triggerRefreshToken()}
                }
            }
        }
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onTimetableSuccess(timetable: MutableMap<SchoolDay, MutableList<SchoolClass>>) {
        mainView?.generateTimetable(timetable)
    }
    override fun onTimetableError(error: KretaError) {
        when (error) {
            is KretaError.VolleyError -> {
                when (error.volleyError) {
                    is AuthFailureError -> {mainView?.triggerRefreshToken()}
                }
            }
        }
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onMessageListSuccess(messageList: List<MessageDescriptor>, sortType: MessageDescriptor.SortType) {
        mainView?.generateMessageDescriptors(messageList.reversed().sortedWith(compareBy(sortType.lambda)))
    }
    override fun onMessageListError(error: KretaError) {
        when (error) {
            is KretaError.VolleyError -> {
                when (error.volleyError) {
                    is AuthFailureError -> {mainView?.triggerRefreshToken()}
                }
            }
        }
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onMessageSuccess(message: MessageDescriptor) {
        mainView?.generateMessage(message)
    }
    override fun onMessageError(error: KretaError) {
        when (error) {
            is KretaError.VolleyError -> {
                when (error.volleyError) {
                    is AuthFailureError -> {mainView?.triggerRefreshToken()}
                }
            }
        }
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
        mainView?.generateTests(testList)
    }
    override fun onTestListError(error: KretaError) {
        when (error) {
            is KretaError.VolleyError -> {
                when (error.volleyError) {
                    is AuthFailureError -> {mainView?.triggerRefreshToken()}
                }
            }
        }
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onHomeworkListSuccess(homeworks: List<Homework>) {
        mainView?.generateHomeworkList(homeworks)
    }
    override fun onHomeworkListError(error: KretaError) {
        when (error) {
            is KretaError.VolleyError -> {
                when (error.volleyError) {
                    is AuthFailureError -> {mainView?.triggerRefreshToken()}
                }
            }
        }
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }

    override fun onStudentDetailsSuccess(studentDetails: StudentDetails) {
        mainView?.generateStudentDetails(studentDetails)
    }
    override fun onStudentDetailsError(error: KretaError) {
        when (error) {
            is KretaError.VolleyError -> {
                when (error.volleyError) {
                    is AuthFailureError -> {mainView?.triggerRefreshToken()}
                }
            }
        }
        mainView?.displayError(error.errorString)
        mainView?.hideProgress()
    }
}
