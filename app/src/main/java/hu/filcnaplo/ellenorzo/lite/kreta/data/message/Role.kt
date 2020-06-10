package hu.filcnaplo.ellenorzo.lite.kreta.data.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Role(
    @Json(name = "Azonosito") val id: Int,
    @Json(name = "kod") val code: String,
    @Json(name = "rovidNev") val shortName: String,
    @Json(name = "nev") val name: String,
    @Json(name = "leiras") val description: String
)
