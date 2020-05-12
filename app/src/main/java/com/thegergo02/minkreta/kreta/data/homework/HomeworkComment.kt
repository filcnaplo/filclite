package com.thegergo02.minkreta.kreta.data.homework

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.kreta.KretaDate

@JsonClass(generateAdapter = true)
data class HomeworkComment (
    @Json(name = "Uid")  val uid: String,
    @Json(name = "FeladasDatuma")  val postDate: KretaDate,
    @Json(name = "FeladatSzovege")  val text: String,
    @Json(name = "IsTanarAltalTorolt")  val isTeacherDeleted: Boolean,
    @Json(name = "IsTanuloAltalTorolt")  val isStudentDeleted: Boolean,
    @Json(name = "RogzitoTanuloNeve")  val author: String,
    @Json(name = "RogzitoTanuloUid")  val studentUid: String
)