package com.thegergo02.minkreta.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.data.sub.*

@JsonClass(generateAdapter = true)
data class Student (
    var accessToken: String = "",
    var refreshToken: String = "",
    @Json(name = "StudentId") val id: Int,
    @Json(name = "SchoolYearId") val schoolYearId: Int,
    @Json(name = "Name") val name: String,
    @Json(name = "NameOfBirth") val nameOfBirth: String,
    @Json(name = "PlaceOfBirth") val placeOfBirth: String,
    @Json(name = "MothersName") val mothersName: String,
    @Json(name = "AddressDataList") val addressDataList: List<String>,
    @Json(name = "DateOfBirthUtc") val DateOfBirthUtc: String,
    @Json(name = "InstituteName") val instituteName: String,
    @Json(name = "InstituteCode") val instituteCode: String,
    @Json(name = "Evaluations") val evaluations: List<Evaluation>,
    @Json(name = "Absences") val absences: List<Absence>,
    @Json(name = "Notes") val notes: List<Note>,
    @Json(name = "OsztalyCsoportok") val classGroups: List<ClassGroup>,
    @Json(name = "Lessons") val lessons: Any? = null,
    @Json(name = "Events") val events: Any? = null,
    @Json(name = "Osztalyfonokok") val classRoomTeachers: List<ClassRoomTeacher>,
    @Json(name = "Tutelaries") val tutelaries: List<Tutelary>
)