package com.thegergo02.minkreta.data.sub

data class Staff(
    val uid: String,
    val name: String,
    val phoneNumbers: Array<String>,
    val emailAddresses: Array<String>
)