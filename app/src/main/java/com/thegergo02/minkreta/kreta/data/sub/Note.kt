package com.thegergo02.minkreta.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.kreta.KretaDate

@JsonClass(generateAdapter = true)
class Note (
    @Json(name = "NoteId")  val id: Int?,
    @Json(name = "Type") val type: String?,
    @Json(name = "Title") val title: String?,
    @Json(name = "Content") val content: String?,
    @Json(name = "SeenByTutelaryUTC") val seenByTutelaryUtc: String?,
    @Json(name = "Teacher") val teacher: String,
    @Json(name = "Date") val date: KretaDate?,
    @Json(name = "CreatingTime") val creatingTime: KretaDate,
    @Json(name = "OsztalyCsoportUid") val classGroupUid: String?
): Comparable<Note> {
    companion object {
        fun sortTypeFromString(str: String): SortType {
            val stringToSortType = mapOf(
                "Teacher" to SortType.Teacher,
                "Creating time" to SortType.CreatingTime,
                "Justification state" to SortType.Type)
            return stringToSortType[str] ?: SortType.CreatingTime
        }
    }

    enum class SortType(val lambda: (it: Note) -> Comparable<*>) {
        CreatingTime({it.creatingTime}),
        Type({it.type ?: ""}),
        Teacher({it.teacher})
    }

    override fun toString(): String {
        return  "$title \n" +
                "$teacher (${date?.toFormattedString(KretaDate.KretaDateFormat.DATE)})"
    }
    fun toDetailedString(): String {
        return  "$title ($type) \n" +
                "$content \n" +
                "$teacher (${date?.toFormattedString(KretaDate.KretaDateFormat.DATETIME)})"
    }
    override fun compareTo(other: Note): Int {
        return this.creatingTime.compareTo(other.creatingTime)
    }
}