package ua.makovskyi.notificator.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by azazellj on 1/11/20.
 */

class AutoNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        message.wrap(applicationContext).show(this)
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
    }
}