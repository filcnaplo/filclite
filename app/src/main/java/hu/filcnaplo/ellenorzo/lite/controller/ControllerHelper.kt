package hu.filcnaplo.ellenorzo.lite.controller

import hu.filcnaplo.ellenorzo.lite.kreta.ErrorReason

class ControllerHelper {
    enum class ControllerOrigin {
        Login,
        Main,
        Message
    }

    enum class RequestOrigin {
        EvaluationList,
        Timetable,
        MessageList,
        Message,
        RefreshToken,
        TestList,
        NoteList,
        NoticeList,
        HomeworkList,
        HomeworkCommentList,
        AbsenceList,
        StudentDetails,
        SendHomework,
        TrashMessage,
        InstituteList,
        Tokens,
        SendMessage,
        Workers,
        SendableReceiverTypes,
        UploadTemporaryAttachment
    }

    companion object {
        //TODO: Translation
        private val reasonToString = mapOf(
            ErrorReason.Empty to "The response was empty!",
            ErrorReason.Unknown to "Unknown error!",
            ErrorReason.NoConnectionError to "There is no internet connection!",
            ErrorReason.Invalid to "The response is invalid!",
            ErrorReason.VolleyError to "Volley encountered an unknown error!"
        )
        private val mainErrorToString = mapOf(
            RequestOrigin.EvaluationList to mapOf(
                ErrorReason.Empty to "There are no evaluations!"
            ),
            RequestOrigin.Timetable to mapOf(
                ErrorReason.Empty to "Your timetable is empty!"
            ),
            RequestOrigin.MessageList to mapOf(
                ErrorReason.Empty to "There are no messages!"
            ),
            RequestOrigin.Message to mapOf(
                ErrorReason.Invalid to "This message is invalid!"
            ),
            RequestOrigin.TestList to mapOf(
                ErrorReason.Empty to "There are no tests!"
            ),
            RequestOrigin.NoteList to mapOf(
                ErrorReason.Empty to "There are no notes!"
            ),
            RequestOrigin.NoticeList to mapOf(
                ErrorReason.Empty to "There are no notices!"
            ),
            RequestOrigin.HomeworkList to mapOf(
                ErrorReason.Empty to "There are no homework!"
            ),
            RequestOrigin.HomeworkCommentList to mapOf(
                ErrorReason.Empty to "There are no comments!"
            ),
            RequestOrigin.AbsenceList to mapOf(
                ErrorReason.Empty to "There are no absences! (Happy Sept. 1.)"
            )
        )

        private val controllerToETS = mapOf(
            ControllerOrigin.Main to mainErrorToString
        )

        fun getErrorString(
            reason: ErrorReason,
            controller: ControllerOrigin,
            request: RequestOrigin
        ): String {
            return controllerToETS[controller]?.get(request)?.get(reason)
                ?: reasonToString[reason] ?: reason.toString()
        }
    }
}