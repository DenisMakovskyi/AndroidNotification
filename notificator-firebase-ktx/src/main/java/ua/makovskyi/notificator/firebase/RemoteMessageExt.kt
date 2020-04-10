package ua.makovskyi.notificator.firebase

import kotlinx.coroutines.runBlocking

import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.net.Uri

import coil.Coil
import coil.api.get

import com.google.firebase.messaging.RemoteMessage

import ua.makovskyi.notificator.data.*
import ua.makovskyi.notificator.utils.defaultNotificationSound
import ua.makovskyi.notificator.utils.isTextMaxLengthExceeded
import ua.makovskyi.notificator.utils.toBundle

/**
 * Created by azazellj, Denis Makovskyi.
 */

fun RemoteMessage.wrap(
    appContext: Context,
    intentionClosure: () -> Intention?
): Notification {
    return notification {
        alarm {
            sound { ofSound() }
            vibrate { ofVibrate() }
            ledLight { ofLEDLight() }
        }
        icons {
            smallIcon { ofSmallIcon(appContext) }
            smallTint { colorFromMetaData(appContext) }
        }
        content {
            color { ofColor() }
            time { ofTime() }
            // Receive title and plain text here, this variables will be needed later.
            val title = ofTitle(appContext)
            val plainText = ofPlainText(appContext)
            title { title }
            plainText { plainText }
            // Trying to select optimal notification style if it is needed.
            // If notification contains image url, ImageStyle is using, otherwise,
            // checking whether title or plain text length is not exceed maximal available text length for notification,
            // then TextStyle is using, in order to keep title and text completely visible for user.
            val imageUrl = ofImage()
            if (imageUrl != null) {
                val bitmap = runBlocking {
                    drawableToBitmap(Coil.get(imageUrl))
                }
                largeIcon { bitmap }
                withImageStyle {
                    behaviour { StyleBehaviour.OVERRIDE }
                    title { title }
                    summary { plainText }
                    largeIcon { null }
                    bigPicture { bitmap }
                }

            } else {
                val isTitleLengthExceeded = title.isTextMaxLengthExceeded()
                if (isTitleLengthExceeded || plainText.isTextMaxLengthExceeded()) {
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
        val manualIntention = intentionClosure()
        if (manualIntention != null) {
            intention = manualIntention
        } else {
            intention {
                autoCancel { ofAutoCancel() }
                ofContentIntent(appContext)?.let {
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

private fun RemoteMessage.ofColor(): Int {
    val colorHex = notification?.color
    return if (colorHex != null) Color.parseColor(colorHex) else 0
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
        NotificationManager.IMPORTANCE_NONE -> Importance.NONE
        NotificationManager.IMPORTANCE_LOW -> Importance.LOW
        NotificationManager.IMPORTANCE_MIN -> Importance.MIN
        NotificationManager.IMPORTANCE_HIGH -> Importance.HIGH
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
        builder.taskStackElement {
            howPut { HowPut.ONLY_NEXT_INTENT }
            intent {
                from { ConstructFrom.ACTION }
                intentAction { clickAction }
                intentExtras { if (data.isNotEmpty()) data.toBundle() else null }
            }
        }
    }
}


private fun RemoteMessage.ofId(): Int {
    return notification?.tag?.hashCode() ?: randomId()
}