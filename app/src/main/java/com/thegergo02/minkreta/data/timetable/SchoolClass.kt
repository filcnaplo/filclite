package com.thegergo02.minkreta.data.timetable

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SchoolClass(
    @Json(name = "LessonId")  val id: Int?,
    @Json(name = "CalendarOraType") val calendarClassType: String?,
    @Json(name = "Count") val count: Int?,
    @Json(name = "Date") val date: String?,
    @Json(name = "StartTime") val startTime: String,
    @Json(name = "EndTime") val endTime: String,
    @Json(name = "Subject") val subject: String?,
    @Json(name = "SubjectCategory") val subjectCategory: String?,
    @Json(name = "SubjectCategoryName") val subjectCategoryName: String?,
    @Json(name = "ClassRoom") val classRoom: String?,
    @Json(name = "ClassGroupUid") val classGroupUid: String?,
    @Json(name = "ClassGroup") val classGroup: String?,
    @Json(name = "Teacher") val teacher: String?,
    @Json(name = "DeputyTeacher") val deputyTeacher: String?,
    @Json(name = "State") val state: String?,
    @Json(name = "StateName") val stateName: String?,
    @Json(name = "PresenceType") val presenceType: String?,
    @Json(name = "PresenceTypeName") val presenceTypeName: String?,
    @Json(name = "TeacherHomeworkId") val teacherHomeworkId: Int?,
    @Json(name = "IsTanuloHaziFeladatEnabled") val isStudentHomeworkEnabled: Boolean?,
    @Json(name = "BejelentettSzamonkeresIdList") val testIdList: List<String>?,
    @Json(name = "Theme") val theme: String?,
    @Json(name = "Nev") val name: String?,
    @Json(name = "Homework") val homework: String?
)
