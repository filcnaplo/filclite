package com.thegergo02.minkreta.data.homework

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.KretaDate

@JsonClass(generateAdapter = true)
data class StudentHomework(
    @Json(name = "Uid")  val uid: Int?,
    @Json(name = "Id")  val id: Int?,
    @Json(name = "TanuloNev")  val studentName: String?,
    @Json(name = "FeladasDatuma")  val postDate: KretaDate?,
    @Json(name = "FeladatSzovege")  val text: String?,
    @Json(name = "RogzitoId")  val postId: Int?,
    @Json(name = "TanuloAltalTorolt")  val deletedByStudent: Boolean?,
    @Json(name = "TanarAltalTorolt")  val deletedByTeacher: Boolean?
)