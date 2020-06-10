package com.thegergo02.filclite.kreta

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.thegergo02.filclite.kreta.adapter.KretaDateAdapter
import com.thegergo02.filclite.kreta.data.Institute
import com.thegergo02.filclite.kreta.data.homework.Homework
import com.thegergo02.filclite.kreta.data.homework.HomeworkComment
import com.thegergo02.filclite.kreta.data.message.LongerMessageDescriptor
import com.thegergo02.filclite.kreta.data.message.MessageDescriptor
import com.thegergo02.filclite.kreta.data.message.TemporaryAttachment
import com.thegergo02.filclite.kreta.data.message.Worker
import com.thegergo02.filclite.kreta.data.sub.*
import com.thegergo02.filclite.kreta.data.timetable.SchoolClass
import com.thegergo02.filclite.kreta.data.timetable.SchoolDay
import com.thegergo02.filclite.kreta.data.timetable.SchoolDayOrder
import com.thegergo02.filclite.kreta.data.timetable.Test
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
        fun makeMessage(messageString: String): LongerMessageDescriptor? {
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<LongerMessageDescriptor> = moshi.adapter(
                LongerMessageDescriptor::class.java)
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

        fun makeNoteList(notesString: String) : List<Note>? {
            val notes = mutableListOf<Note>()
            val notesJson = JSONArray(notesString)
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<Note> = moshi.adapter(
                Note::class.java)
            for (i in 0 until notesJson.length()) {
                val note = adapter.fromJson(notesJson[i].toString())
                if (note != null) {
                    notes.add(note)
                }
            }
            return if (notes.isEmpty()) null else notes
        }

        fun makeNoticeList(noticeString: String) : List<Notice>? {
            val notices = mutableListOf<Notice>()
            val noticesJson = JSONArray(noticeString)
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<Notice> = moshi.adapter(
                Notice::class.java)
            for (i in 0 until noticesJson.length()) {
                val notice = adapter.fromJson(noticesJson[i].toString())
                if (notice != null) {
                    notices.add(notice)
                }
            }
            return if (notices.isEmpty()) null else notices
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

        fun makeHomeworkCommentList(homeworkCommentsString: String) : List<HomeworkComment>? {
            val homeworkComments = mutableListOf<HomeworkComment>()
            val homeworkCommentsJson = JSONArray(homeworkCommentsString)
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<HomeworkComment> = moshi.adapter(
                HomeworkComment::class.java)
            for (i in 0 until homeworkCommentsJson.length()) {
                val comment = adapter.fromJson(homeworkCommentsJson[i].toString())
                if (comment != null) {
                    homeworkComments.add(comment)
                }
            }
            return if (homeworkComments.isEmpty()) null else homeworkComments
        }

        fun makeAbsenceList(absencesString: String) : List<Absence>? {
            val absences = mutableListOf<Absence>()
            val absencesJson = JSONArray(absencesString)
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<Absence> = moshi.adapter(
                Absence::class.java)
            for (i in 0 until absencesJson.length()) {
                val absence = adapter.fromJson(absencesJson[i].toString())
                if (absence != null) {
                    absences.add(absence)
                }
            }
            return if (absences.isEmpty()) null else absences
        }

        fun makeStudentDetails(studentDetailsString: String): StudentDetails? {
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<StudentDetails> = moshi.adapter(
                StudentDetails::class.java)
            return adapter.fromJson(studentDetailsString)
        }

        fun makeWorkers(workersString: String, type: Type): List<Worker>? {
            val workers = mutableListOf<Worker>()
            val workersJson = JSONArray(workersString)
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<Worker> = moshi.adapter(
                Worker::class.java)
            for (i in 0 until workersJson.length()) {
                val worker = adapter.fromJson(workersJson[i].toString())
                if (worker != null) {
                    worker.type = type
                    workers.add(worker)
                }
            }
            return if (workers.isEmpty()) null else workers
        }

        fun makeTypes(typesString: String): List<Type>? {
            val types = mutableListOf<Type>()
            val typesJson = JSONArray(typesString)
            val moshi: Moshi = Moshi.Builder().add(KretaDateAdapter()).build()
            val adapter: JsonAdapter<Type> = moshi.adapter(
                Type::class.java)
            for (i in 0 until typesJson.length()) {
                val type = adapter.fromJson(typesJson[i].toString())
                if (type != null) {
                    types.add(type)
                }
            }
            return if (types.isEmpty()) null else types
        }

        fun temporaryIdFromString(response: String): String {
            val moshi: Moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<TemporaryAttachment> = moshi.adapter(TemporaryAttachment::class.java)
            return adapter.fromJson(response)?.id ?: ""
        }
    }
}