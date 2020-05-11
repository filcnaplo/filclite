package com.thegergo02.minkreta.kreta.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.kreta.KretaDate

@JsonClass(generateAdapter = true)
class Institute (
    @Json(name = "instituteId")  val id: Int,
    @Json(name = "instituteCode") val code: String,
    @Json(name = "name") val name: String,
    @Json(name = "city") val city: String,
    @Json(name = "url") val url: String,
    @Json(name = "advertisingUrl") val advertisingUrl: String?,
    @Json(name = "informationImageUrl") val informationImageUrl: String?,
    @Json(name = "informationUrl") val informationUrl: String?,
    @Json(name = "featureToggleSet") val featureSet: Map<String, String>?
) {
    override fun toString(): String {
        return code
    }
}