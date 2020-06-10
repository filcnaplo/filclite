package hu.filcnaplo.ellenorzo.lite.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClassRoomTeacher(
    @Json(name = "Uid") val uid: String?,
    @Json(name = "Tanar") val teacher: Map<String, String>?,
    @Json(name = "Osztalyai") val classes: List<Map<String, String>>?
)