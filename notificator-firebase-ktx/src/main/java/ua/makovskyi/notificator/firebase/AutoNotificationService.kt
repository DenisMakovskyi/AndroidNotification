package ua.makovskyi.notificator.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import ua.makovskyi.notificator.data.Intention

/**
 * Created by azazellj on 1/11/20.
 */

open class AutoNotificationService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.wrap(applicationContext) { onManualIntention(message) }.show(this)
    }

    open fun onManualIntention(message: RemoteMessage): Intention? = null
}