package com.thegergo02.minkreta.ui

import com.thegergo02.minkreta.kreta.data.homework.Homework
import com.thegergo02.minkreta.kreta.data.message.MessageDescriptor
import com.thegergo02.minkreta.kreta.data.sub.Absence
import com.thegergo02.minkreta.kreta.data.sub.Evaluation
import com.thegergo02.minkreta.kreta.data.sub.Note
import com.thegergo02.minkreta.kreta.data.sub.Notice

data class SortType(
    val abs: Absence.SortType? = null,
    val note: Note.SortType? = null,
    val eval: Evaluation.SortType? = null,
    val messageDescriptor: MessageDescriptor.SortType? = null,
    val notice: Notice.SortType? = null,
    val homework: Homework.SortType? = null
)