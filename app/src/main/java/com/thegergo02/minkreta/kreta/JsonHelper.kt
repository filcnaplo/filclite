package com.thegergo02.minkreta.kreta

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.thegergo02.minkreta.kreta.adapter.KretaDateAdapter
import com.thegergo02.minkreta.kreta.data.Institute
import com.thegergo02.minkreta.kreta.data.homework.Homework
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.kreta.data.sub.Evaluation
import com.thegergo02.minkreta.kreta.data.timetable.SchoolClass
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDay
import com.thegergo02.minkreta.kreta.data.timetable.SchoolDayOrder
import com.thegergo02.minkreta.kreta.data.timetable.Test
import org.json.JSONArray

class JsonHelper {
    companion object {
        fun makeInstitutes(instituteArray: JSONArray): List<Institute>? {
            val moshi: Moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<Institute> = moshi.adapter(Institute::class.java)
            val instituteList = mutableListOf<Institute>()
            for (i in 0 until instituteArray.length()) {
                val instituteString = instituteArray[i].toString()
                val institute = adapter.fromJson(instituteString)
                if (institute != null) {
                    instituteList.add(institute)
                }
            }
            return if (instituteList.isEmpty()) null else instituteList
        }

        fun makeTokens(tokensString: String): Map<String, String>? {
            val moshi: Moshi = Moshi.Builder().build()
            val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
            val adapter: JsonAdapter<Map<String, String>> = moshi.adapter(type)
            val tokens = adapter.fromJson(tokensString)
            return tokens
        }

        fun makeTimetable(timetableString: String): MutableMap<SchoolDay, MutableList<SchoolClass>>? {
            val timetable = mutableMapOf<SchoolDay, MutableList<SchoolClass>>()
            val timetableJson = JSONArray(timetableString)
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<SchoolClass> = moshi.adapter(
                SchoolClass::class.java)
            for (day in SchoolDayOrder.schoolDayOrder)
            {
                timetable[day] = mutableListOf()
            }
            for (i in 0 until timetableJson.length()) {
                val schoolClass = adapter.fromJson(timetableJson[i].toString())
                if (schoolClass != null) {
                    val schoolDay = schoolClass.startDate.toSchoolDay()
                    timetable[schoolDay]?.add(schoolClass)
                }
            }
            for (day in SchoolDayOrder.schoolDayOrder) {
                val schoolDay = timetable[day]
                if (schoolDay != null)
                    if (schoolDay.isEmpty()) {
                        timetable.remove(day)
                    }
            }
            return if (timetable.isEmpty()) null else timetable
        }

        fun makeMessageList(messageListString: String): List<MessageDescriptor>? {
            if (messageListString.isEmpty()) {
                return null
            }
            val messageListJson = JSONArray(messageListString)
            val messageList = mutableListOf<MessageDescriptor>()
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<MessageDescriptor> = moshi.adapter(
                MessageDescriptor::class.java)
            for (i in 0 until messageListJson.length()) {
                val messageDescriptor = adapter.fromJson(messageListJson[i].toString())
                if (messageDescriptor != null) {
                    messageList.add(messageDescriptor)
                }
            }
            return if (messageList.isEmpty()) null else messageList
        }
        fun makeMessage(messageString: String): MessageDescriptor? {
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<MessageDescriptor> = moshi.adapter(
                MessageDescriptor::class.java)
            return adapter.fromJson(messageString)
        }

        fun makeTestList(testListString: String): List<Test>? {
            val testList = mutableListOf<Test>()
            val testsJson = JSONArray(testListString)
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<Test> = moshi.adapter(
                Test::class.java)
            for (i in 0 until testsJson.length()) {
                val test = adapter.fromJson(testsJson[i].toString())
                if (test != null) {
                    testList.add(test)
                }
            }
            return if (testList.isEmpty()) null else testList
        }

        fun makeEvaluationList(evaluationsString: String) : List<Evaluation>? {
            val evals = mutableListOf<Evaluation>()
            val evalsJson = JSONArray(evaluationsString)
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<Evaluation> = moshi.adapter(
                Evaluation::class.java)
            for (i in 0 until evalsJson.length()) {
                val eval = adapter.fromJson(evalsJson[i].toString())
                if (eval != null) {
                    evals.add(eval)
                }
            }
            return if (evals.isEmpty()) null else evals
        }

        fun makeHomeworkList(homeworksString: String) : List<Homework>? {
            val homeworks = mutableListOf<Homework>()
            val homeworksJson = JSONArray(homeworksString)
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<Homework> = moshi.adapter(
                Homework::class.java)
            for (i in 0 until homeworksJson.length()) {
                val homework = adapter.fromJson(homeworksJson[i].toString())
                if (homework != null) {
                    homeworks.add(homework)
                }
            }
            return if (homeworks.isEmpty()) null else homeworks
        }
    }
}