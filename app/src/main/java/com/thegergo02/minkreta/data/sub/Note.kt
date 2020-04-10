package com.thegergo02.minkreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.KretaDate

@JsonClass(generateAdapter = true)
data class Note (
    @Json(name = "NoteId")  val id: Int?,
    @Json(name = "Type") val type: String?,
    @Json(name = "Title") val title: String?,
    @Json(name = "Content") val content: String?,
    @Json(name = "SeenByTutelaryUTC") val seenByTutelaryUtc: String?,
    @Json(name = "Teacher") val teacher: String?,
    @Json(name = "Date") val date: KretaDate?,
    @Json(name = "CreatingTime") val creatingTime: String?,
    @Json(name = "OsztalyCsoportUid") val classGroupUid: String?
)