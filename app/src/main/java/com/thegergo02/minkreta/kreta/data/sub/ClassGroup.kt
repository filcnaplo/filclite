package com.thegergo02.minkreta.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClassGroup(
    @Json(name = "Nev") val name: String?,
    @Json(name = "Uid") val uid: String
)