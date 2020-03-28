package com.thegergo02.minkreta.data.sub

data class Evaluation(
    val id: Int,
    val form: String,
    val formName: String,
    val type: String,
    val typeName: String,
    val subject: String,
    val subjectCategory: String,
    val subjectCategoryName: String,
    val theme: String,
    val countsIntoAverage: Boolean,
    val mode: String,
    val weight: String,
    val value: String,
    val numberValue: Int,
    val seenByTutelaryUtc: Any,
    val teacher: String,
    val date: String,
    val creatingTime: String,
    val nature: Nature,
    val natureName: String,
    val valueType: Nature,
    val classGroupUid: String
)