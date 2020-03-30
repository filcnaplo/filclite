package com.thegergo02.minkreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Teacher(
    @Json(name = "Uid") val uid: String?,
    @Json(name = "Alkalmazott") val staff: Staff?
)