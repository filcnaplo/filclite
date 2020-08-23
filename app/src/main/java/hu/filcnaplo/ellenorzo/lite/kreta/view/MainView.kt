package hu.filcnaplo.ellenorzo.lite.view

import hu.filcnaplo.ellenorzo.lite.kreta.StudentDetails
import hu.filcnaplo.ellenorzo.lite.kreta.data.homework.Homework
import hu.filcnaplo.ellenorzo.lite.kreta.data.homework.HomeworkComment
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.LongerMessageDescriptor
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.MessageDescriptor
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Absence
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Evaluation
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Note
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Notice
import hu.filcnaplo.ellenorzo.lite.kreta.data.timetable.SchoolClass
import hu.filcnaplo.ellenorzo.lite.kreta.data.timetable.SchoolDay
import hu.filcnaplo.ellenorzo.lite.kreta.data.timetable.Test

interface MainView {
    fun hideProgress()
    fun showProgress()
    fun generateTimetable(timetable: List<SchoolClass>?)
    fun displayError(error: String)
    fun displaySuccess(success: String)
    fun generateMessageDescriptors(messages: List<MessageDescriptor>)
    fun generateMessage(message: LongerMessageDescriptor)
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