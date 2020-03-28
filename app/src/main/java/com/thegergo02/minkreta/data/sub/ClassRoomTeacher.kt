package com.thegergo02.minkreta.data.sub

data class ClassRoomTeacher(
    val uid: String,
    val teacher: Teacher,
    val classes: Map<String, String>
)