package com.thegergo02.minkreta.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tutelary(
    @Json(name = "Uid") val uid: Int,
    @Json(name = "Nev") val name: String?,
    @Json(name = "EmailCim") val email: String?,
    @Json(name = "Telefonszam") val phoneNumber: String?
)