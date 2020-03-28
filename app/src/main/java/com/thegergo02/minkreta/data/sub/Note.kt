package com.thegergo02.minkreta.data.sub

data class Note (
    val id: Int,
    val type: String,
    val title: String,
    val content: String,
    val seenByTutelaryUtc: Any,
    val teacher: String,
    val date: String,
    val creatingTime: String,
    val classGroupUid: String
)