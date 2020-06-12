package hu.filcnaplo.ellenorzo.lite.kreta.data.timetable

enum class SchoolDay() {
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday,
    Sunday
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