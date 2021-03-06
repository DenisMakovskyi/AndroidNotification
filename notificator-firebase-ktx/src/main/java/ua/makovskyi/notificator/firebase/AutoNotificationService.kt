package ua.makovskyi.notificator.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import ua.makovskyi.notificator.data.Intention

/**
 * Created by azazellj, Denis Makovskyi.
 */

open class AutoNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        message.wrap(applicationContext) { onManualIntention(message) }.show(this)
    }

    open fun onManualIntention(message: RemoteMessage): Intention? = null
}