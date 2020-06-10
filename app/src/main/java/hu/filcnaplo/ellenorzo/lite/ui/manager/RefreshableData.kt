package hu.filcnaplo.ellenorzo.lite.ui.manager

import hu.filcnaplo.ellenorzo.lite.kreta.data.homework.Homework
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.MessageDescriptor
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Absence
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Evaluation
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Note
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Notice
import hu.filcnaplo.ellenorzo.lite.kreta.data.timetable.Test

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