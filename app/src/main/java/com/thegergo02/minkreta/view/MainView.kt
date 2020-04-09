package com.thegergo02.minkreta.view

import com.thegergo02.minkreta.data.Student
import com.thegergo02.minkreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.data.timetable.Test
import org.json.JSONObject

interface MainView {
    fun setStudent(student: Student)
    fun hideProgress()
    fun showProgress()
    fun generateTimetable(timetable: Map<SchoolDay, List<SchoolClass>>)
    fun displayError(error: String)
    fun generateMessageDescriptors(messages: List<MessageDescriptor>)
    fun generateMessage(message: MessageDescriptor)
    fun generateTests(tests: List<Test>)
    fun triggerRefreshToken()
    fun refreshToken(tokensJson: JSONObject)
    fun sendToLogin()
}