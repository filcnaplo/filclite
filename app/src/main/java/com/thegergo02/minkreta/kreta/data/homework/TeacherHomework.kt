package com.thegergo02.minkreta.kreta.data.homework

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.kreta.KretaDate

@JsonClass(generateAdapter = true)
data class TeacherHomework(
    @Json(name = "Uid")  val uid: String?,
    @Json(name = "Id")  val id: Int?,
    @Json(name = "OsztalyCsoportUid")  val classGroupUid: String?,
    @Json(name = "Tantargy")  val subject: String?,
    @Json(name = "Rogzito")  val poster: String?,
    @Json(name = "isTanarRogzitette")  val isTeacherPost: Boolean?,
    @Json(name = "Oraszam")  val classCount: Int?,
    @Json(name = "TanitasiOraId")  val classId: Int?,
    @Json(name = "Szoveg")  val text: String?,
    @Json(name = "FeladasDatuma")  val postDate: KretaDate?,
    @Json(name = "Hatarido")  val deadline: KretaDate?,
    @Json(name = "IsTanuloHaziFeladatEnabled") val isStudentHomeworkEnabled: Boolean?
)