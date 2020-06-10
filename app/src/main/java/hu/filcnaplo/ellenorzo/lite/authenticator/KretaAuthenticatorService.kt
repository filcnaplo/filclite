package hu.filcnaplo.ellenorzo.lite.authenticator

import android.app.Service
import android.content.Intent
import android.os.IBinder


class KretaAuthenticatorService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        val authenticator = KretaAuthenticator(this)
        return authenticator.iBinder
    }
}
