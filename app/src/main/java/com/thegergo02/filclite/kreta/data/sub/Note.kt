package com.thegergo02.filclite.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.filclite.kreta.KretaDate

@JsonClass(generateAdapter = true)
class Note (
    @Json(name = "Uid") val uid: String,
    @Json(name = "Cim") val title: String,
    @Json(name = "Datum") val date: KretaDate,
    @Json(name = "KeszitesDatuma") val creatingDate: KretaDate,
    @Json(name = "KeszitoTanarNeve") val teacher: String,
    @Json(name = "LattamozasDatuma") val seenDate: KretaDate?,
    @Json(name = "OsztalyCsoport") val classGroup: ClassGroup?,
    @Json(name = "Tartalom") val text: String,
    @Json(name = "Tipus") val type: Nature
): Comparable<Note> {
    companion object {
        fun sortTypeFromString(str: String): SortType {
            val stringToSortType = mapOf(
                "Teacher" to SortType.Teacher,
                "Creating time" to SortType.Date,
                "Justification state" to SortType.Type)
            return stringToSortType[str] ?: SortType.Date
        }
    }

    enum class SortType(val lambda: (it: Note) -> Comparable<*>) {
        Date({it.date}),
        Type({it.type.name ?: ""}),
        Teacher({it.teacher})
    }

    override fun toString(): String {
        return  "$title \n" +
                "$teacher (${date?.toFormattedString(KretaDate.KretaDateFormat.DATE)})"
    }
    fun toDetailedString(): String {
        return  "$title (${type.description}) \n" +
                "$text \n" +
                "$teacher (${date?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)})"
    }
    override fun compareTo(other: Note): Int {
        return this.date.compareTo(other.date)
    }
}