package hu.filcnaplo.ellenorzo.lite.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Staff(
    @Json(name = "Uid") val uid: String?,
    @Json(name = "Nev") val name: String?,
    @Json(name = "Telefonok") val phoneNumbers: List<String>?,
    @Json(name = "Emailek") val emailAddresses: List<String>?
)