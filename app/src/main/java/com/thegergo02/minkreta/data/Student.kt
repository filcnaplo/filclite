package com.thegergo02.minkreta.data

import com.thegergo02.minkreta.data.sub.*

data class Student (
    val id: Int,
    val schoolYearId: Int,
    val name: String,
    val nameOfBirth: String,
    val placeOfBirth: String,
    val mothersName: String,
    val addressDataList: Array<String>,
    val DateOfBirthUtc: String,
    val instituteName: String,
    val instituteCode: String,
    val evaluations: Array<Evaluation>,
    val absences: Array<Absence>,
    val notes: Array<Note>,
    val classGroups: Array<ClassGroup>,
    val lessons: Any,
    val events: Any,
    val classRoomTeachers: Array<ClassRoomTeacher>,
    val tutelaries: Array<Tutelary>
)