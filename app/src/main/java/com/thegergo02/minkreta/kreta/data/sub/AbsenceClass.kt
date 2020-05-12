package com.thegergo02.minkreta.kreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.kreta.KretaDate

@JsonClass(generateAdapter = true)
data class AbsenceClass (
    @Json(name = "KezdoDatum")  val startDate: KretaDate,
    @Json(name = "VegDatum")  val endDate: KretaDate,
    @Json(name = "Oraszam")  val classCount: Int
)