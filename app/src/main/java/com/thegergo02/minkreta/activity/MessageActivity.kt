package com.thegergo02.minkreta.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.controller.MessageController
import com.thegergo02.minkreta.kreta.KretaRequests
import com.thegergo02.minkreta.kreta.data.message.Attachment
import com.thegergo02.minkreta.kreta.data.message.Receiver
import com.thegergo02.minkreta.kreta.data.sub.Type
import com.thegergo02.minkreta.ui.UIHelper
import com.thegergo02.minkreta.view.MessageView
import kotlinx.android.synthetic.main.activity_message.*


class MessageActivity : AppCompatActivity(), MessageView {
    private val OPEN_REQUEST_CODE = 69

    private lateinit var controller: MessageController
    private var attachments = mutableListOf<Attachment>()
    private var replyId: Int? = null

    private var receiverTypes = listOf<Type>()
    private var selectableReceivers = listOf<Receiver>()
    private var receivers = mutableListOf<Receiver>()

    private var lockReceiverSpinner = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val adapterReceiverType =
            ArrayAdapter(this, R.layout.sorter_spinner_item, listOf("Loading receiver types..."))
        adapterReceiverType.setDropDownViewResource(R.layout.sorter_spinner_dropdown_item)
        val adapterReceivers =
            ArrayAdapter(this, R.layout.sorter_spinner_item, listOf("Choose a receiver type first..."))
        adapterReceivers.setDropDownViewResource(R.layout.sorter_spinner_dropdown_item)
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
                if (!lockReceiverSpinner) {
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
                Log.w("attach", attachment.toString())
            }
        }
        controller.getSendableReceiverTypes()
    }

    override fun generateReceiverList(receivers: List<Receiver>) {
        selectableReceivers = receivers
        val receiverStrings = mutableListOf<String>()
        receiverStrings.add("")
        for (receiver in receivers) {
            receiverStrings.add(receiver.name)
        }
        lockReceiverSpinner = true
        val adapterReceiver =
            ArrayAdapter(this, R.layout.sorter_spinner_item, receiverStrings)
        adapterReceiver.setDropDownViewResource(R.layout.sorter_spinner_dropdown_item)
        receiver_s.adapter = adapterReceiver
    }

    override fun generateSendableReceiverTypes(types: List<Type>) {
        this.receiverTypes = types
        val typeStrings = mutableListOf<String>()
        for (type in types) {
            typeStrings.add(type.name)
        }
        val adapterReceiverType =
            ArrayAdapter(this, R.layout.sorter_spinner_item, typeStrings)
        adapterReceiverType.setDropDownViewResource(R.layout.sorter_spinner_dropdown_item)
        receivertype_s.adapter = adapterReceiverType
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_REQUEST_CODE && resultCode == Activity.RESULT_OK) data?.let {
            if (data.clipData == null) {
                val file = (data.data ?: Uri.EMPTY).toFile()
                attachments.add(Attachment(0, file.name, null, file))
            } else {
                val itemCount = data.clipData?.itemCount ?: 0
                for (i in 0 until itemCount) {
                    val uri = data.clipData?.getItemAt(i)?.uri
                    if (uri != null) {
                        val file = uri.toFile()
                        attachments.add(Attachment(i, file.name, null, file))
                    }
                }
            }
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
