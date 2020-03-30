package com.thegergo02.minkreta.data.sub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClassGroup(
    @Json(name = "OktatasNevelesiFeladat") val eduMission: Map<String, String>?,
    @Json(name = "OktatasNevelesiKategoria") val eduCategory: Map<String, String>?,
    @Json(name = "Nev") val name: String?,
    @Json(name = "OsztalyCsoportTipus") val classGroupType: String?,
    @Json(name = "IsAktiv") val isActive: Boolean?,
    @Json(name = "Uid") val uid: String?,
    @Json(name = "OsztalyfonokUid") val classRoomTeacherId: String?
)