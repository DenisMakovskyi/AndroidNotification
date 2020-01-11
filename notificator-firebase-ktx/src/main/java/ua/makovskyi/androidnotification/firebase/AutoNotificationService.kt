package ua.makovskyi.androidnotification.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ua.makovskyi.notificator.Notificator

/**
 * Created by azazellj on 1/11/20.
 */
class AutoNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        Notificator.showNotification(this, message.wrap(applicationContext))
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
    }
}