package com.thegergo02.minkreta.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClassRoomTeacher(
    @Json(name = "Uid") val uid: String?,
    @Json(name = "Tanar") val teacher: Teacher?,
    @Json(name = "Osztalyai") val classes: List<Map<String, String>>?
)