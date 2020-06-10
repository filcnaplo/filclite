package com.thegergo02.filclite.kreta.data.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TemporaryAttachment(
    @Json(name = "fajlAzonosito") val id: String,
    @Json(name = "utvonal") val path: String,
    @Json(name = "fajlMeretByteLength") val byteLength: Long
)
