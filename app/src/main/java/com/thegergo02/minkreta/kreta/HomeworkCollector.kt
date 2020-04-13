package com.thegergo02.minkreta.kreta

import android.util.Log
import com.thegergo02.minkreta.activity.MainActivity
import com.thegergo02.minkreta.kreta.data.homework.StudentHomework
import com.thegergo02.minkreta.kreta.data.homework.TeacherHomework

class HomeworkCollector(private val listener: OnHomeworkListFinished, private val homeworkIdSize: Int)
    : KretaRequests.OnStudentHomeworkResult,
      KretaRequests.OnTeacherHomeworkResult {
    private var studentHomeworkList = mutableListOf<StudentHomework>()
    private var studentHomeworkSize = 0
    private var teacherHomeworkList = mutableListOf<TeacherHomework>()
    private var teacherHomeworkSize = 0

    init {
        if (homeworkIdSize == 0) {
            listener.onHomeworkListSuccess(studentHomeworkList, teacherHomeworkList)
        }
    }

    interface OnHomeworkListFinished {
        fun onHomeworkListSuccess(studentHomeworkList: List<StudentHomework>, teacherHomeworkList: List<TeacherHomework>)
        fun onHomeworkListError(error: KretaError)
    }

    fun getTeacherHomeworkListener(): KretaRequests.OnTeacherHomeworkResult {
        return this
    }
    fun getStudentHomeworkListener(): KretaRequests.OnStudentHomeworkResult {
        return this
    }

    override fun onStudentHomeworkSuccess(studentHomework: List<StudentHomework?>?) {
        if (studentHomework != null) {
            for (homework in studentHomework) {
                if (homework != null) {
                    studentHomeworkList.add(homework)
                }
            }
        }
        studentHomeworkSize++
        if (homeworkIdSize == studentHomeworkSize) {
            homeworkCollectionDone()
        }
    }
    override fun onStudentHomeworkError(error: KretaError) {
        listener.onHomeworkListError(error)
    }

    override fun onTeacherHomeworkSuccess(teacherHomework: TeacherHomework?) {
        if (teacherHomework != null) {
            teacherHomeworkList.add(teacherHomework)
        }
        teacherHomeworkSize++
        if (homeworkIdSize == teacherHomeworkSize) {
            homeworkCollectionDone()
        }
    }
    override fun onTeacherHomeworkError(error: KretaError) {
        listener.onHomeworkListError(error)
    }

    private fun homeworkCollectionDone() {
        if (homeworkIdSize == studentHomeworkSize && homeworkIdSize == teacherHomeworkSize) {
            listener.onHomeworkListSuccess(studentHomeworkList, teacherHomeworkList)
        }
    }
}