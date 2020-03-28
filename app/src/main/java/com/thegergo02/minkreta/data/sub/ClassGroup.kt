package com.thegergo02.minkreta.data.sub

data class ClassGroup(
    val eduMission: Map<String, String>,
    val eduCategory: Map<String, String>,
    val name: String,
    val classGroupType: String,
    val isActive: Boolean,
    val uid: String,
    val classRoomTeacherId: String
)