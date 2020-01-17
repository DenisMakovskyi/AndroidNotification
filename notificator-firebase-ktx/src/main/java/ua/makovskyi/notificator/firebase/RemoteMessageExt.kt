package ua.makovskyi.notificator.firebase

import kotlinx.coroutines.runBlocking

import android.app.NotificationManager
import android.content.Context
import android.net.Uri

import coil.Coil
import coil.api.get

import com.google.firebase.messaging.RemoteMessage

import ua.makovskyi.notificator.data.*
import ua.makovskyi.notificator.utils.defaultNotificationSound
import ua.makovskyi.notificator.utils.isPlainTextMaxLengthExceeded
import ua.makovskyi.notificator.utils.isTitleMaxLengthExceeded
import ua.makovskyi.notificator.utils.toBundle

/**
 * Created by azazellj on 1/11/20.
 */

fun RemoteMessage.wrap(
    applicationContext: Context,
    intentionClosure: (() -> Intention)? = null
): Notification {
    return notification {
        alarm {
            sound { ofSound() }
            vibrate { ofVibrate() }
            ledLight { ofLEDLight() }
        }
        icons {
            smallIcon { ofSmallIcon(applicationContext) }
        }
        content {
            time { ofTime() }
            // Receive title and plain text here, this variables will be needed later.
            val title = ofTitle(applicationContext)
            val plainText = ofPlainText(applicationContext)
            title { title }
            plainText { plainText }
            // Trying to select optimal notification style if it is needed.
            // If notification contains image url, ImageStyle is using, otherwise,
            // checking whether title or plain text length is not exceed maximal available text length for notification,
            // then TextStyle is using, in order to keep title and text in single line.
            val imageUrl = ofImage()
            if (imageUrl != null) {
                val bitmap = runBlocking {
                    drawableToBitmap(Coil.get(imageUrl))
                }
                largeIcon { bitmap }
                withImageStyle {
                    behaviour { StyleBehaviour.IGNORE }
                    summary { plainText }
                    bigPicture { bitmap }
                }

            } else {
                val isTitleLengthExceeded = title.isTitleMaxLengthExceeded()
                if (isTitleLengthExceeded || plainText.isPlainTextMaxLengthExceeded()) {
                    withTextStyle {
                        title { title }
                        bigText { plainText }
                        // Trying to select optimal behaviour.
                        // If title max available length for notification was exceeded -
                        // style will override title when notification will expand.
                        behaviour {
                            if (isTitleLengthExceeded) StyleBehaviour.OVERRIDE else StyleBehaviour.IGNORE
                        }
                    }
                }
            }
        }
        channel {
            importance { ofImportance() }
            channelInfo {
                channelId { ofChannelId() }
            }
        }
        if (intentionClosure != null) {
            intention = intentionClosure()
        } else {
            intention {
                autoCancel { ofAutoCancel() }
                ofContentIntent(applicationContext)?.let {
                    contentIntent(it)
                }
            }
        }
        identifier {
            id { ofId() }
        }
    }
}

private fun RemoteMessage.ofSound(): Uri {
    if (notification?.defaultSound == true) return defaultNotificationSound()
    return notification?.sound?.let { Uri.parse(it) } ?: defaultNotificationSound()
}

private fun RemoteMessage.ofVibrate(): LongArray? {
    if (notification?.defaultVibrateSettings == true) return null
    return notification?.vibrateTimings
}

private fun RemoteMessage.ofLEDLight(): LEDLight? {
    val notification = notification ?: return null

    if (notification.defaultLightSettings) return null

    val lightSettings = notification.lightSettings ?: return null

    return LEDLight(
        argb = lightSettings[0],
        onMs = lightSettings[1],
        offMs = lightSettings[2])
}

private fun RemoteMessage.ofSmallIcon(context: Context): Int {
    val iconResName = notification?.icon ?: return iconFromMetaData(context)
    return iconFromResources(context, iconResName)
}

private fun RemoteMessage.ofTime(): Long? {
    return notification?.eventTime
}

private fun RemoteMessage.ofTitle(context: Context): String? {
    val titleLocKey = notification?.titleLocalizationKey

    if (titleLocKey.isNullOrEmpty()) return notification?.title

    return stringFromResources(context, titleLocKey).also { title ->
        val titleLocArgs = notification?.titleLocalizationArgs
        if (!titleLocArgs.isNullOrEmpty()) String.format(title, titleLocArgs)
    }
}

private fun RemoteMessage.ofPlainText(context: Context): String? {
    val bodyLocKey = notification?.bodyLocalizationKey

    if (bodyLocKey.isNullOrEmpty()) return notification?.body

    return stringFromResources(context, bodyLocKey).also { body ->
        val bodyLocArgs = notification?.bodyLocalizationArgs
        if (!bodyLocArgs.isNullOrEmpty()) String.format(body, bodyLocArgs)
    }
}

private fun RemoteMessage.ofImage(): String? {
    return notification?.imageUrl?.toString()
}

private fun RemoteMessage.ofImportance(): Importance {
    return when(notification?.notificationPriority) {
        NotificationManager.IMPORTANCE_LOW -> Importance.LOW
        NotificationManager.IMPORTANCE_MIN -> Importance.MIN
        NotificationManager.IMPORTANCE_HIGH -> Importance.HIGHT
        NotificationManager.IMPORTANCE_MAX -> Importance.MAXIMAL
        else -> Importance.DEFAULT
    }
}

private fun RemoteMessage.ofChannelId(): String {
    return notification?.channelId ?: "CHANNEL_GENERAL"
}

private fun RemoteMessage.ofAutoCancel(): Boolean {
    return notification?.sticky ?: true
}

private fun RemoteMessage.ofContentIntent(context: Context): PendingIntentBuilder? {
    val clickAction = notification?.clickAction ?: return null

    return PendingIntentBuilder().also { builder ->
        builder.targetIntent { From.ACTIVITY }
        builder.packageContext { context }
        builder.taskStackElements(
            taskStackElement {
                howPut { HowPut.ONLY_NEXT_INTENT }
                intent {
                    from { ConstructFrom.ACTION }
                    intentAction { clickAction }
                    intentExtras { if (data.isNotEmpty()) data.toBundle() else null }
                }
            }
        )
    }
}


private fun RemoteMessage.ofId(): Int {
    return notification?.tag?.hashCode() ?: randomId()
}