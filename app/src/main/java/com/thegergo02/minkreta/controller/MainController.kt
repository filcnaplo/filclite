package com.thegergo02.minkreta.controller

import com.android.volley.*
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.thegergo02.minkreta.ApiHandler
import com.thegergo02.minkreta.KretaDate
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.data.Student
import com.thegergo02.minkreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.data.timetable.SchoolDayOrder
import com.thegergo02.minkreta.misc.Strings
import com.thegergo02.minkreta.view.MainView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.*

class MainController(private var mainView: MainView?, private val apiHandler: ApiHandler)
    : ApiHandler.OnFinishedResult {

    private lateinit var currentStudent: Student
    fun getStudent(accessToken: String, refreshToken: String, instituteCode: String) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getStudent(parentListener, accessToken, refreshToken, instituteCode)
        }
    }

    fun getTimetable(accessToken: String, refreshToken: String, instituteCode: String, fromDate: KretaDate, toDate: KretaDate) {
        val parentListener = this
        GlobalScope.launch {
            apiHandler.getTimetable(parentListener, accessToken, refreshToken, instituteCode, fromDate.toString(), toDate.toString())
        }
    }

    override fun onStudentSuccess(student: String, accessToken: String, refreshToken: String) {
        val moshi: Moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<Student> = moshi.adapter(Student::class.java)
        val newStudent = adapter.fromJson(student)
        if (newStudent != null) currentStudent = newStudent
        currentStudent.accessToken = accessToken
        currentStudent.refreshToken = refreshToken
        mainView?.setStudent(currentStudent)
    }
    override fun onStudentError(error: VolleyError) {
        val errorString: String = when(error) {
            is AuthFailureError -> Strings.get(R.string.auth_failure_error_general)
            is TimeoutError -> Strings.get(R.string.timeout_error_general)
            is NetworkError -> Strings.get(R.string.network_error_general)
            is NoConnectionError -> Strings.get(R.string.no_connection_error_general)
            else -> error.toString()
        }
        mainView?.displayError(errorString)
        mainView?.hideProgress()
    }

    override fun onTokensSuccess(tokens: String) {}
    override fun onTokensError(error: VolleyError) {}
    override fun onApiLinkSuccess(link: String) {}
    override fun onApiLinkError(error: String) {}
    override fun onInstitutesSuccess(institutes: JSONArray) {}
    override fun onInstitutesError(error: VolleyError) {}
    override fun onTimetableSuccess(timetable: String) {
        val returnedTimetable = mutableMapOf<SchoolDay, MutableList<SchoolClass>>()
        val timetableJson = JSONArray(timetable)
        val moshi: Moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<SchoolClass> = moshi.adapter(SchoolClass::class.java)
        val calendar = Calendar.getInstance()

        for (day in SchoolDayOrder.schoolDayOrder)
        {
            returnedTimetable[day] = mutableListOf()
        }
        for (i in 0 until timetableJson.length()) {
            val schoolClass = adapter.fromJson(timetableJson[i].toString())
            if (schoolClass != null) {
                val kretaDate = KretaDate(schoolClass.startTime)
                val schoolDay =
                    SchoolDayOrder.schoolDayOrder[kretaDate.toLocalDateTime().dayOfWeek.value - 1]
                returnedTimetable[schoolDay]?.add(schoolClass)
            }
        }
        mainView?.generateTimetable(returnedTimetable)
    }
    override fun onTimetableError(error: VolleyError) {
        val errorString: String = when(error) {
            is AuthFailureError -> Strings.get(R.string.auth_failure_error_general)
            is TimeoutError -> Strings.get(R.string.timeout_error_general)
            is NetworkError -> Strings.get(R.string.network_error_general)
            is NoConnectionError -> Strings.get(R.string.no_connection_error_general)
            else -> error.toString()
        }
        mainView?.displayError(errorString)
        mainView?.hideProgress()
    }
}
