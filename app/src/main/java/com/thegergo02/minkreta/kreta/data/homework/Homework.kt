package com.thegergo02.minkreta.kreta.data.homework

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thegergo02.minkreta.kreta.KretaDate
import com.thegergo02.minkreta.kreta.data.sub.ClassGroup

@JsonClass(generateAdapter = true)
data class Homework(
    @Json(name = "Uid")  val uid: String,
    @Json(name = "FeladasDatuma")  val postDate: KretaDate,
    @Json(name = "HataridoDatuma")  val deadlineDate: KretaDate,
    @Json(name = "RogzitesIdopontja")  val pinDate: KretaDate,
    @Json(name = "IsTanarRogzitette")  val teacherHomework: Boolean,
    @Json(name = "IsTanuloHaziFeladatEnabled")  val isStudentHomeworkEnabled: Boolean,
    @Json(name = "RogzitoTanarNeve")  val teacher: String,
    @Json(name = "TantargyNeve")  val subject: String,
    @Json(name = "Szoveg")  val text: String,
    @Json(name = "OsztalyCsoport")  val classGroup: ClassGroup,
    @Json(name = "OpenBoardKepek")  val pictures: List<String>?
)