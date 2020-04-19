package com.thegergo02.minkreta.kreta.data.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Attachment(
    @Json(name = "azonosito") val id: Int,
    @Json(name = "fajlNev") val fileName: String
)
