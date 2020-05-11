package com.thegergo02.minkreta.view

import com.thegergo02.minkreta.kreta.data.homework.StudentHomework
import com.thegergo02.minkreta.kreta.data.homework.TeacherHomework
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.kreta.data.sub.Evaluation
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.kreta.data.timetable.Test

interface MainView {
    fun hideProgress()
    fun showProgress()
    fun generateTimetable(timetable: Map<SchoolDay, List<SchoolClass>>)
    fun displayError(error: String)
    fun generateMessageDescriptors(messages: List<MessageDescriptor>)
    fun generateMessage(message: MessageDescriptor)
    fun generateTests(tests: List<Test>)
    fun generateEvaluationList(evaluations: List<Evaluation>)
    fun triggerRefreshToken()
    fun refreshToken(tokens: Map<String, String>)
    fun sendToLogin()
    fun generateHomeworkList(studentHomeworkList: List<StudentHomework>, teacherHomeworkList: List<TeacherHomework>)
}