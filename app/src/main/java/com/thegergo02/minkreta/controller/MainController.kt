package com.thegergo02.minkreta.controller

import com.android.volley.AuthFailureError
import com.android.volley.VolleyError
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.thegergo02.minkreta.kreta.HomeworkCollector
import com.thegergo02.minkreta.kreta.data.Student
import com.thegergo02.minkreta.kreta.data.homework.StudentHomework
import com.thegergo02.minkreta.kreta.data.homework.TeacherHomework
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.kreta.data.timetable.Test
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.kreta.KretaError
import com.thegergo02.minkreta.kreta.KretaRequests
import com.thegergo02.minkreta.kreta.adapter.KretaDateAdapter
import com.thegergo02.minkreta.view.MainView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray

class MainController(private var mainView: MainView?, private val apiHandler: KretaRequests)
    : KretaRequests.OnStudentResult,
    KretaRequests.OnRefreshTokensResult,
    KretaRequests.OnTimetableResult,
    KretaRequests.OnMessageListResult,
    KretaRequests.OnMessageResult,
    KretaRequests.OnTestListResult,
    HomeworkCollector.OnHomeworkListFinished {

    fun getStudent(accessToken: String, instituteCode: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getStudent(parentListener, accessToken, instituteCode)
        }
    }

    fun getTimetable(accessToken: String, instituteCode: String, fromDate: KretaDate, toDate: KretaDate) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTimetable(parentListener, accessToken, instituteCode, fromDate, toDate)
        }
    }

    fun getMessageList(accessToken: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getMessageList(parentListener, accessToken)
        }
    }
    fun getMessage(accessToken: String, messageId: Int) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getMessage(parentListener, accessToken, messageId)
        }
    }
    fun setMessageRead(accessToken: String, messageId: Int, isRead: Boolean = true) {
        GlobalScope.launch {
            apiHandler.setMessageRead(accessToken, messageId, isRead)
        }
    }

    fun refreshToken(refreshToken: String, instituteUrl: String, instituteCode: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.refreshToken(parentListener, refreshToken, instituteUrl, instituteCode)
        }
    }

    fun getTestList(accessToken: String, instituteUrl: String, fromDate: KretaDate, toDate: KretaDate) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTestList(parentListener, accessToken, instituteUrl, fromDate, toDate)
        }
    }

    fun getHomework(accessToken: String, instituteUrl: String, homeworkIds: List<Int>) {
        val homeworkCollector = HomeworkCollector(this, homeworkIds.size)
        GlobalScope.launch {
            apiHandler.getHomework(homeworkCollector, accessToken, instituteUrl, homeworkIds)
        }
    }

    override fun onStudentSuccess(student: Student) {
        mainView?.setStudent(student)
    }
    override fun onStudentError(error: KretaError) {
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

    override fun onMessageListSuccess(messageList: List<MessageDescriptor>) {
        mainView?.generateMessageDescriptors(messageList.reversed())
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

    override fun onHomeworkListSuccess(studentHomeworkList: List<StudentHomework>, teacherHomeworkList: List<TeacherHomework>) {
        mainView?.generateHomeworkList(studentHomeworkList, teacherHomeworkList)
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
}
