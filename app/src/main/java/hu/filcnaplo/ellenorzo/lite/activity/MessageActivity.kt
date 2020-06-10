package hu.filcnaplo.ellenorzo.lite.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import hu.filcnaplo.ellenorzo.lite.R
import hu.filcnaplo.ellenorzo.lite.controller.MessageController
import hu.filcnaplo.ellenorzo.lite.kreta.KretaRequests
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.Attachment
import hu.filcnaplo.ellenorzo.lite.kreta.data.message.Receiver
import hu.filcnaplo.ellenorzo.lite.kreta.data.sub.Type
import hu.filcnaplo.ellenorzo.lite.ui.ThemeHelper
import hu.filcnaplo.ellenorzo.lite.ui.UIHelper
import hu.filcnaplo.ellenorzo.lite.view.MessageView
import kotlinx.android.synthetic.main.activity_message.*


class MessageActivity : AppCompatActivity(), MessageView {
    private val OPEN_REQUEST_CODE = 69

    private lateinit var controller: MessageController
    private lateinit var themeHelper: ThemeHelper
    private var attachments = mutableListOf<Attachment>()
    private var replyId: Int? = null

    private var receiverTypes = listOf<Type>()
    private var selectableReceivers = listOf<Receiver>()
    private var receivers = mutableListOf<Receiver>()

    private var lockReceiverSpinner = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeHelper = ThemeHelper(this)
        themeHelper.setCurrentTheme()
        setContentView(R.layout.activity_message)

        val sharedPref = getSharedPreferences(getString(R.string.auth_path), Context.MODE_PRIVATE) ?: return
        val accessToken = sharedPref.getString("accessToken", null)
        val refreshToken = sharedPref.getString("refreshToken", null)
        val instituteCode = sharedPref.getString("instituteCode", null)
        if (accessToken != null && refreshToken != null && instituteCode != null) {
            controller = MessageController(this, this, accessToken, refreshToken, instituteCode)
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
        message_btt.setOnClickListener {
            controller.sendMessage(
                receivers,
                attachments,
                subject_mea.text.toString(),
                message_mea.text.toString(),
                replyId
            )
        }
        val spinnerLayouts = themeHelper.getResourcesFromAttributes(listOf(R.attr.sortSpinnerItemLayout, R.attr.sortSpinnerDropdownItemLayout))
        val adapterReceiverType =
            ArrayAdapter(this, spinnerLayouts[0], listOf("Loading receiver types..."))
        adapterReceiverType.setDropDownViewResource(spinnerLayouts[1])
        val adapterReceivers =
            ArrayAdapter(this, spinnerLayouts[0], listOf("Choose a receiver type first..."))
        adapterReceivers.setDropDownViewResource(spinnerLayouts[1])
        receivertype_s.adapter = adapterReceiverType
        receivertype_s.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //ARISZTOKRETA FEJLESZTO (X3NIXBOI), COWARE APPS, ALDJON MEG AZ URISTEN
                val bridgeMap = mapOf(
                    1 to 9,
                    2 to 8,
                    3 to 7,
                    4 to 1,
                    5 to 2,
                    6 to 10,
                    7 to 11
                )
                if (!receiverTypes.isEmpty()) {
                    controller.getReceivers(
                        KretaRequests.ReceiverType.values()
                            .firstOrNull { it.type.id == bridgeMap[receiverTypes[position].id] }
                            ?: KretaRequests.ReceiverType.Teacher)
                }
            }
        }
        receiver_s.adapter = adapterReceivers
        receiver_s.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!lockReceiverSpinner && position != 0) {
                    if (!selectableReceivers.isEmpty()) {
                        val receiver = selectableReceivers[position - 1]
                        if (!receivers.contains(receiver)) {
                            receivers.add(receiver)
                        }
                    }
                    var receiversText = ""
                    for (receiver in receivers) {
                        receiversText += "${receiver.name}, "
                    }
                    receivers_tv.setText(receiversText)
                    receivers_tv.setSelection(receivers_tv.length())
                } else {
                    lockReceiverSpinner = false
                }
            }
        }
        attachment_btt.setOnClickListener {
            val chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.type = "*/*"
            chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            val intent = Intent.createChooser(chooseFile, "Add attachment")
            startActivityForResult(intent, OPEN_REQUEST_CODE)
        }
        message_btt.setOnClickListener {
            for (attachment in attachments) {
                controller.uploadTemporaryAttachment(attachment)
            }
        }
        controller.getSendableReceiverTypes()
    }

    override fun generateReceiverList(receivers: List<Receiver>) {
        selectableReceivers = receivers
        val receiverStrings = mutableListOf<String>()
        receiverStrings.add("Choose a receiver...")
        for (receiver in receivers) {
            receiverStrings.add(receiver.name)
        }
        lockReceiverSpinner = true
        val spinnerLayouts = themeHelper.getResourcesFromAttributes(listOf(R.attr.sortSpinnerItemLayout, R.attr.sortSpinnerDropdownItemLayout))
        val adapterReceiver =
            ArrayAdapter(this, spinnerLayouts[0], receiverStrings)
        adapterReceiver.setDropDownViewResource(spinnerLayouts[1])
        receiver_s.adapter = adapterReceiver
    }

    override fun generateSendableReceiverTypes(types: List<Type>) {
        val spinnerLayouts = themeHelper.getResourcesFromAttributes(listOf(R.attr.sortSpinnerItemLayout, R.attr.sortSpinnerDropdownItemLayout))
        this.receiverTypes = types
        val typeStrings = mutableListOf<String>()
        for (type in types) {
            typeStrings.add(type.name)
        }
        val adapterReceiverType =
            ArrayAdapter(this, spinnerLayouts[0], typeStrings)
        adapterReceiverType.setDropDownViewResource(spinnerLayouts[1])
        receivertype_s.adapter = adapterReceiverType
    }

    private fun uriToAttachment(uri: Uri, id: Int = 0): Attachment {
        val inputStream = contentResolver.openInputStream(uri)
        val fileName = contentResolver.query(uri, null, null, null, null)?.use {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(index)
        }
        return Attachment(id, fileName ?: "", null, inputStream)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_REQUEST_CODE && resultCode == Activity.RESULT_OK) data?.let {
            if (data.clipData == null) {
                val uri = data.data ?: Uri.EMPTY
                attachments.add(uriToAttachment(uri))
            } else {
                val itemCount = data.clipData?.itemCount ?: 0
                for (i in 0 until itemCount) {
                    val uri = data.clipData?.getItemAt(i)?.uri
                    if (uri != null) {
                        attachments.add(uriToAttachment(uri, i))
                    }
                }
            }
            var attachmentsText = ""
            for (attachment in attachments) {
                attachmentsText += "${attachment.fileName}, "
            }
            attachment_mea.setText(attachmentsText)
            attachment_mea.setSelection(attachment_mea.length())
        }
    }

    override fun assignAttachmentTemporaryId(attachment: Attachment) {
        for (currentAttachment in attachments) {
            if (currentAttachment.id == attachment.id) {
                currentAttachment.temporaryId = attachment.temporaryId
            }
        }
        var isSendable = true
        for (attachment in attachments) {
            if (attachment.temporaryId == null) {
                isSendable = false
                break
            }
        }
        if (isSendable) {
            controller.sendMessage(receivers, attachments, subject_mea.text.toString(), message_mea.text.toString())
        }
    }

    override fun displayError(error: String) {
        UIHelper.displayError(this, message_cl, error)
    }

    override fun displaySuccess(success: String) {
        UIHelper.displaySuccess(this, message_cl, success)
    }

    override fun backToMain() {
        finish()
    }
}
