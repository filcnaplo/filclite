package com.thegergo02.minkreta.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Subject(
    @Json(name = "Uid") val uid: String,
    @Json(name = "Kategoria") val category: Nature,
    @Json(name = "Nev") val name: String
)
