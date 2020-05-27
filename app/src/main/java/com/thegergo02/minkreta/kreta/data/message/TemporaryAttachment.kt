package com.thegergo02.minkreta.kreta.data.message

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.File
import java.io.InputStream

@JsonClass(generateAdapter = true)
data class TemporaryAttachment(
    @Json(name = "fajlAzonosito") val id: String,
    @Json(name = "utvonal") val path: String,
    @Json(name = "fajlMeretByteLength") val byteLength: Long
)
