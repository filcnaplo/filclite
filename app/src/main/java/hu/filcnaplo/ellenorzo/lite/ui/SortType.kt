package hu.filcnaplo.ellenorzo.lite.ui

import hu.filcnaplo.ellenorzo.lite.kreta.data.homework.Homework
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.MessageDescriptor
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Absence
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Evaluation
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Note
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Notice

data class SortType(
    val abs: Absence.SortType? = null,
    val note: Note.SortType? = null,
    val eval: Evaluation.SortType? = null,
    val messageDescriptor: MessageDescriptor.SortType? = null,
    val notice: Notice.SortType? = null,
    val homework: Homework.SortType? = null
)