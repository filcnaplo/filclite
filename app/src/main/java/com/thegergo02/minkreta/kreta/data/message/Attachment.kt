package com.thegergo02.minkreta.kreta.data.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Attachment(
    @Json(name = "azonosito") val id: Int,
    @Json(name = "fajlNev") val fileName: String,
    val temporaryId: String?
) {
    override fun toString(): String {
        return "{\"fajlNev\": \"${fileName}\", \"azonosito\":\"${id}\", \"fajl\": {\"ideiglenesFajlAzonosito\":\"${temporaryId}\"}}"
    }
}
