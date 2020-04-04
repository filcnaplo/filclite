package com.thegergo02.minkreta

import java.time.LocalDateTime

class KretaDate(localDateTime: LocalDateTime = LocalDateTime.now()) {
    var year: Int = 1970
    var month: Int = 1
    var day: Int = 1
    var hour: Int = 0
    var minute: Int = 0
    var second: Int = 0
    init {
        year = localDateTime.year
        month = localDateTime.monthValue
        day = localDateTime.dayOfMonth
        hour = localDateTime.hour
        minute = localDateTime.minute
        second = localDateTime.second
    }

    constructor(year: Int = 1970, month: Int = 1, day: Int = 1, hour: Int = 0, minute: Int = 0, second: Int = 0,
                localDateTime: LocalDateTime = LocalDateTime.now())
            : this(localDateTime) {
        this.year = year
        this.month = month
        this.day = day
        this.hour = hour
        this.minute = minute
        this.second = second
    }

    constructor(date: String, localDateTime: LocalDateTime = LocalDateTime.now()) : this(localDateTime) {
        fromString(date)
    }

    private fun fromString(str: String?) {
        val dateAndTime = str?.split("-","T",":")
        if (dateAndTime != null) {
            year = dateAndTime[0].toInt()
            month = dateAndTime[1].toInt()
            day = dateAndTime[2].toInt()
            hour = dateAndTime[3].toInt()
            minute = dateAndTime[4].toInt()
            second = dateAndTime[5].toInt()
        }
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
}