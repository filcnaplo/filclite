package hu.filcnaplo.ellenorzo.lite.ui

import hu.filcnaplo.ellenorzo.lite.kreta.data.homework.Homework
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.MessageDescriptor
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Absence
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Evaluation
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Note
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Notice

/*val abs: Absence.SortType? = null,
val note: Note.SortType? = null,
val eval: Evaluation.SortType? = null,
val messageDescriptor: MessageDescriptor.SortType? = null,
val notice: Notice.SortType? = null,
val homework: Homework.SortType? = null*/

class SortType {
    var abs: Absence.SortType? = null
    var note: Note.SortType? = null
    var eval: Evaluation.SortType? = null
    var messageDescriptor: MessageDescriptor.SortType? = null
    var notice: Notice.SortType? = null
    var homework: Homework.SortType? = null

    constructor(abs: Absence.SortType) {
        this.abs = abs
    }
    constructor(eval: Evaluation.SortType) {
        this.eval = eval
    }
    constructor(note: Note.SortType) {
        this.note = note
    }
    constructor(messageDescriptor: MessageDescriptor.SortType) {
        this.messageDescriptor = messageDescriptor
    }
    constructor(notice: Notice.SortType) {
        this.notice = notice
    }
    constructor(homework: Homework.SortType) {
        this.homework = homework
    }
}