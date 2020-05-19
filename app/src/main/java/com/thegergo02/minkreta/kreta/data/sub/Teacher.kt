package com.thegergo02.minkreta.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Teacher(
    @Json(name = "kretaAzonosito") val kretaId: Int,
    @Json(name = "isAlairo") val isSigner: Boolean,
    @Json(name = "nev") val name: String,
    @Json(name = "titulus") val role: String
    )