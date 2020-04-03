package com.thegergo02.minkreta.data.timetable

enum class SchoolDay(day: String) {
    Monday("Monday"),
    Tuesday("Tuesday"),
    Wednesday("Wednesday"),
    Thursday("Thursday"),
    Friday("Friday"),
    Saturday("Saturday"),
    Sunday("Sunday")
}

object SchoolDayOrder {
    val schoolDayOrder = listOf(
        SchoolDay.Monday,
        SchoolDay.Tuesday,
        SchoolDay.Wednesday,
        SchoolDay.Thursday,
        SchoolDay.Friday,
        SchoolDay.Saturday,
        SchoolDay.Sunday
    )
}