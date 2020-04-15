package com.thegergo02.minkreta.kreta

import android.util.Log
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDayOrder
import java.time.DayOfWeek
import java.time.LocalDateTime

class KretaDate(year: Int = 1970, month: Int = 1, day: Int = 1, hour: Int = 0, minute: Int = 0, second: Int = 0): Comparable<KretaDate> {
    var year: Int = 1970
    var month: Int = 1
    var day: Int = 1
    var hour: Int = 0
    var minute: Int = 0
    var second: Int = 0
    init {
       this.year = year
       this.month = month
       this.day = day
       this.hour = hour
       this.minute = minute
       this.second = second
    }

    constructor(localDateTime: LocalDateTime = LocalDateTime.now(), year: Int = 1970, month: Int = 1, day: Int = 1, hour: Int = 0, minute: Int = 0, second: Int = 0)
            : this(year, month, day, hour, second, minute) {
       this.year = localDateTime.year
       this.month = localDateTime.monthValue
       this.day = localDateTime.dayOfMonth
       this.hour = localDateTime.hour
       this.minute = localDateTime.minute
       this.second = localDateTime.second
    }

    constructor(date: String, year: Int = 1970, month: Int = 1, day: Int = 1, hour: Int = 0, minute: Int = 0, second: Int = 0) : this(year, month, day, hour, second, minute) {
        fromString(date)
    }

    fun fromSchoolDay(schoolDay: SchoolDay): KretaDate {
        val firstDay = LocalDateTime.now().with(DayOfWeek.MONDAY)
        for (i in (0..6)) {
            firstDay.plusDays(i.toLong())
            if (firstDay.dayOfWeek.toString() == schoolDay.toString().toUpperCase()) {
                year = firstDay.year
                month = firstDay.monthValue
                day = firstDay.dayOfMonth
                hour = firstDay.hour
                minute = firstDay.minute
                second = firstDay.second
                break
            }
        }
        return this
    }

    fun fromString(str: String?): KretaDate {
        val dateAndTime = str?.split("-","T",":")
        if (dateAndTime != null && str.isNotBlank()) {
            year = dateAndTime[0].toInt()
            month = dateAndTime[1].toInt()
            day = dateAndTime[2].toInt()
            hour = dateAndTime[3].toInt()
            minute = dateAndTime[4].toInt()
            second = dateAndTime[5].replace("Z", "").replaceAfter(".", "").replace(".", "").toInt()
        }
        return this
    }
    private fun decideZero(num: Int): String {
        return if (num < 10) {
            "0${num}"
        } else {
            num.toString()
        }
    }
    override fun toString(): String {
        return "${decideZero(year)}-${decideZero(month)}-${decideZero(day)}T${decideZero(hour)}:${decideZero(minute)}:${decideZero(second)}"
    }

    fun toLocalDateTime(): LocalDateTime {
        return LocalDateTime.of(year, month, day, hour, minute, second)
    }

    fun toSchoolDay(): SchoolDay {
        return SchoolDayOrder.schoolDayOrder[toLocalDateTime().dayOfWeek.value - 1]
    }

    enum class KretaDateFormat {
        DATE,
        TIME,
        DATETIME
    }
    fun toFormattedString(format: KretaDateFormat): String {
        val dateString = "${decideZero(year)}. ${decideZero(month)}. ${decideZero(day)}."
        val timeString = "${decideZero(hour)}:${decideZero(minute)}:${decideZero(second)}"
        return when(format) {
            KretaDateFormat.DATE -> dateString
            KretaDateFormat.TIME -> timeString
            KretaDateFormat.DATETIME -> "$dateString $timeString"
        }
    }

    fun isToday(): Boolean {
        return (toSchoolDay().toString().toUpperCase() == LocalDateTime.now().dayOfWeek.toString())
    }

    override fun compareTo(other: KretaDate): Int {
        val differences = listOf(other.year - this.year,
            other.month - this.month,
            other.day - this.day,
            other.hour - this.hour,
            other.minute - this.minute,
            other.second - this.second)
        for (difference in differences) {
            if (difference == 0) {
                continue
            }
            return difference
        }
        return 0
    }
}
