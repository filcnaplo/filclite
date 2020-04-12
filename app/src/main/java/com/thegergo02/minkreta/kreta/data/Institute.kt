package com.thegergo02.minkreta.kreta.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.kreta.KretaDate

@JsonClass(generateAdapter = true)
class Institute (
    @Json(name = "InstituteId")  val id: Int,
    @Json(name = "InstituteCode") val code: String,
    @Json(name = "Name") val name: String,
    @Json(name = "Url") val url: String,
    @Json(name = "City") val city: String,
    @Json(name = "AdvertisingUrl") val advertisingUrl: String?,
    @Json(name = "FeatureToggleSet") val featureSet: Map<String, String>?
) {
    override fun toString(): String {
        return code
    }
}