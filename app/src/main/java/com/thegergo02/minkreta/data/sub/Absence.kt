package com.thegergo02.minkreta.data.sub

data class Absence (
    val id: Int,
    val type: String,
    val typeName: String,
    val mode: String,
    val modeName: String,
    val subject: String,
    val subjectCategory: String,
    val subjectCategoryName: String,
    val delayTime: Int,
    val teacher: String,
    val lessonStartTime: String,
    val numberOfLessons: Int,
    val creatingTime: String,
    val JustificationState: String,
    val JustificationStateName: String,
    val JustificationType: String,
    val JustificationTypeName: String,
    val seenByTutelaryUtc: Any,
    val classGroupUid: String
)