package com.thegergo02.minkreta.kreta.data.message

import android.net.Uri
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.File
import java.io.InputStream

@JsonClass(generateAdapter = true)
class Attachment(
    @Json(name = "azonosito") val id: Int,
    @Json(name = "fajlNev") val fileName: String,
    var temporaryId: String?,
    @Transient var inputStream: InputStream? = null
) {
    override fun toString(): String {
        return "{\"fajlNev\": \"${fileName}\", \"azonosito\":\"${id}\", \"fajl\": {\"ideiglenesFajlAzonosito\":\"${temporaryId}\"}}"
    }
}
