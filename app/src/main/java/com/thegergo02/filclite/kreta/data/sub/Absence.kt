@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection")

package com.thegergo02.filclite.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.filclite.kreta.KretaDate

@JsonClass(generateAdapter = true)
class Absence (
    @Json(name = "Uid") val uid: String,
    @Json(name = "IgazolasAllapota") val justificationState: String,
    @Json(name = "IgazolasTipusa") val justificationType: Nature,
    @Json(name = "KesesPercben") val delay: Int?,
    @Json(name = "KeszitesDatuma") val creatingDate: KretaDate,
    @Json(name = "Mod") val mode: Nature,
    @Json(name = "Datum") val date: KretaDate,
    @Json(name = "Ora") val absenceClass: AbsenceClass,
    @Json(name = "RogzitoTanarNeve") val teacher: String,
    @Json(name = "Tantargy") val subject: Subject,
    @Json(name = "Tipus") val type: Nature,
    @Json(name = "OsztalyCsoport") val classGroup: ClassGroup
): Comparable<Absence> {
    companion object {
        fun sortTypeFromString(str: String): SortType {
            val stringToSortType = mapOf(
                "Subject" to SortType.Subject,
                "Teacher" to SortType.Teacher,
                "Lesson start time" to SortType.ClassStartDate,
                "Creating time" to SortType.Date,
                "Justification state" to SortType.JustificationState)
            return stringToSortType[str] ?: SortType.Subject
        }
    }

    enum class SortType(val lambda: (it: Absence) -> Comparable<*>) {
        Date({it.date}),
        ClassStartDate({it.absenceClass.startDate}),
        JustificationState({it.justificationState}),
        Teacher({it.teacher}),
        Subject({it.subject.name}),
    }

    override fun toString(): String {
        return "$subject ($teacher) \n" +
                absenceClass.startDate.toFormattedString(KretaDate.KretaDateFormat.DATE)
    }
    fun toDetailedString(): String {
        return  "${type.description} \n" +
                "$subject ($teacher) \n" +
                "$justificationState (${justificationType.description}) \n" +
                "${absenceClass.startDate.toFormattedString(KretaDate.KretaDateFormat.DATETIME)} \n" +
                date.toFormattedString(KretaDate.KretaDateFormat.DATE)
    }

    override fun compareTo(other: Absence): Int {
        return this.date.compareTo(other.date)
    }
}