package com.thegergo02.minkreta.view

import com.thegergo02.minkreta.kreta.StudentDetails
import com.thegergo02.minkreta.kreta.data.homework.Homework
import com.thegergo02.minkreta.kreta.data.homework.HomeworkComment
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.kreta.data.sub.Absence
import com.thegergo02.minkreta.kreta.data.sub.Evaluation
import com.thegergo02.minkreta.kreta.data.sub.Note
import com.thegergo02.minkreta.kreta.data.sub.Notice
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
    fun generateTestList(tests: List<Test>)
    fun generateEvaluationList(evaluations: List<Evaluation>)
    fun refreshToken(tokens: Map<String, String>)
    fun sendToLogin()
    fun generateHomeworkList(homeworks: List<Homework>)
    fun generateStudentDetails(studentDetails: StudentDetails)
    fun generateNoteList(notes: List<Note>)
    fun generateNoticeList(notes: List<Notice>)
    fun generateAbsenceList(absences: List<Absence>)
    fun generateHomeworkCommentList(homeworkComments: List<HomeworkComment>)
    fun refreshCommentList(homeworkUid: String)
}