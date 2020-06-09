package com.thegergo02.minkreta.authenticator

import android.app.Service
import android.content.Intent
import android.os.IBinder


class KretaAuthenticatorService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        val authenticator = KretaAuthenticator(this)
        return authenticator.iBinder
    }
}
