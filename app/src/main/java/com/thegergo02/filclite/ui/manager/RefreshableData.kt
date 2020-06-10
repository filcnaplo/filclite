package com.thegergo02.filclite.ui.manager

import com.thegergo02.filclite.kreta.data.homework.Homework
import com.thegergo02.filclite.kreta.data.message.MessageDescriptor
import com.thegergo02.filclite.kreta.data.sub.Absence
import com.thegergo02.filclite.kreta.data.sub.Evaluation
import com.thegergo02.filclite.kreta.data.sub.Note
import com.thegergo02.filclite.kreta.data.sub.Notice
import com.thegergo02.filclite.kreta.data.timetable.Test

data class RefreshableData(
    val text: String,
    val absence: Absence? = null,
    val eval: Evaluation? = null,
    val homework: Homework? = null,
    val messageDescriptor: MessageDescriptor? = null,
    val note: Note? = null,
    val notice: Notice? = null,
    val test: Test? = null//,
//    val timetable: ? = null
)