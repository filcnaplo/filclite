package hu.filcnaplo.ellenorzo.lite.kreta.data.message

import com.squareup.moshi.Json

data class MessageType(
    @Json(name = "azonosito") val id: Int,
    @Json(name = "kod") val code: String?,
    @Json(name = "rovidNev") val shortName: String?,
    @Json(name = "nev") val name: String?,
    @Json(name = "leiras") val description: String?
)