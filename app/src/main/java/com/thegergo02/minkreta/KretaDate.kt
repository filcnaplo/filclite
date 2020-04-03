package com.thegergo02.minkreta

import com.thegergo02.minkreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.data.timetable.SchoolDay
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.math.min

class KretaDate(localDateTime: LocalDateTime = LocalDateTime.now()) {
    var year = 1970
    var month = 1
    var day = 1
    var hour = 0
    var minute = 0
    var second = 0

    init {
        year = localDateTime.year
        month = localDateTime.monthValue
        day = localDateTime.dayOfMonth
        hour = localDateTime.hour
        minute = localDateTime.minute
        second = localDateTime.second
    }

    fun fromString(str: String?): KretaDate {
        val dateAndTime = str?.split("-","T",":")
        if (dateAndTime != null) {
            year = dateAndTime[0].toInt()
            month = dateAndTime[1].toInt()
            day = dateAndTime[2].toInt()
            hour = dateAndTime[3].toInt()
            minute = dateAndTime[4].toInt()
            second = dateAndTime[5].toInt()
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
}