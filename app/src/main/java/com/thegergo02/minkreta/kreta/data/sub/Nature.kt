package com.thegergo02.minkreta.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Nature(
    @Json(name = "Id") val id: Int?,
    @Json(name = "Nev") val name: String?,
    @Json(name = "Leiras") val description: String?
)