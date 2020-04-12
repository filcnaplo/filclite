package com.thegergo02.minkreta.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tutelary(
    @Json(name = "TutelaryId") val id: Int?,
    @Json(name = "Name") val name: String?,
    @Json(name = "Email") val email: String?,
    @Json(name = "PhoneNumber") val phoneNumber: String?
)