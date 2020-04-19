@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection")

package com.thegergo02.minkreta.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.kreta.KretaDate
import java.lang.ref.Reference
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

@JsonClass(generateAdapter = true)
class Absence (
    @Json(name = "AbsenceId") val id: Int?,
    @Json(name = "Type") val type: String?,
    @Json(name = "TypeName") val typeName: String?,
    @Json(name = "Mode") val mode: String?,
    @Json(name = "ModeName") val modeName: String?,
    @Json(name = "Subject") val subject: String,
    @Json(name = "SubjectCategory") val subjectCategory: String?,
    @Json(name = "SubjectCategoryName") val subjectCategoryName: String?,
    @Json(name = "DelayTimeMinutes") val delayTime: Int?,
    @Json(name = "Teacher") val teacher: String,
    @Json(name = "LessonStartTime") val lessonStartTime: KretaDate,
    @Json(name = "NumberOfLessons") val numberOfLessons: Int?,
    @Json(name = "CreatingTime") val creatingTime: KretaDate,
    @Json(name = "JustificationState") val justificationState: String?,
    @Json(name = "JustificationStateName") val justificationStateName: String?,
    @Json(name = "JustificationType") val justificationType: String?,
    @Json(name = "JustificationTypeName") val justificationTypeName: String?,
    @Json(name = "SeenByTutelaryUTC") val seenByTutelaryUtc: String?,
    @Json(name = "OsztalyCsoportUid") val classGroupUid: String?
): Comparable<Absence> {
    companion object {
        fun sortTypeFromString(str: String): SortType {
            val stringToSortType = mapOf(
                "Subject" to SortType.Subject,
                "Teacher" to SortType.Teacher,
                "Lesson start time" to SortType.LessonStartTime,
                "Creating time" to SortType.CreatingTime,
                "Justification state" to SortType.JustificationState)
            return stringToSortType[str] ?: SortType.Subject
        }
    }

    enum class SortType(val lambda: (it: Absence) -> Comparable<*>) {
        CreatingTime({it.creatingTime}),
        LessonStartTime({it.lessonStartTime}),
        JustificationState({it.justificationState ?: ""}),
        Teacher({it.teacher}),
        Subject({it.subject}),
    }

    override fun toString(): String {
        return "$subject ($teacher) \n" +
                "${lessonStartTime?.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
    }
    fun toDetailedString(): String {
        return  "$typeName \n" +
                "$subject ($teacher) \n" +
                "$modeName \n" +
                "${lessonStartTime?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)} \n" +
                "${creatingTime?.toFormattedString(KretaDate.KretaDateFormat.DATE)}"
    }

    override fun compareTo(other: Absence): Int {
        return this.creatingTime.compareTo(other.creatingTime)
    }
}