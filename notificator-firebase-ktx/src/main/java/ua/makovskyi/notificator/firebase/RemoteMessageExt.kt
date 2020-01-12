package ua.makovskyi.notificator.firebase

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import com.google.firebase.messaging.RemoteMessage
import ua.makovskyi.notificator.data.*

/**e
 * Created by azazellj on 1/11/20.
 */

fun RemoteMessage.ofSound(): Uri? {
    if (notification?.defaultSound == true) return null
    return notification?.sound?.let { Uri.parse(it) }
}

fun RemoteMessage.ofVibrateTimings(): LongArray? {
    if (notification?.defaultVibrateSettings == true) return null
    return notification?.vibrateTimings
}

fun RemoteMessage.ofLEDLight(): LEDLight? {
    val notification = notification ?: return null

    if (notification.defaultLightSettings) return null

    val lightSettings = notification.lightSettings ?: return null

    return LEDLight(
        argb = lightSettings[0],
        onMs = lightSettings[1],
        offMs = lightSettings[2]
    )
}

fun RemoteMessage.ofImportance(): Importance? {
    val importance = notification?.notificationPriority ?: return null

    return when (importance) {
        NotificationManager.IMPORTANCE_UNSPECIFIED -> Importance.DEFAULT
        NotificationManager.IMPORTANCE_MAX -> Importance.MAXIMAL
        NotificationManager.IMPORTANCE_HIGH -> Importance.HIGHT
        NotificationManager.IMPORTANCE_LOW -> Importance.LOW
        NotificationManager.IMPORTANCE_MIN -> Importance.MIN
        else -> Importance.DEFAULT
    }
}

fun RemoteMessage.ofChannelInfo(): ChannelInfo.Builder? {
    return notification?.channelId?.let { ChannelInfo.Builder(channelId = it) }
}

fun RemoteMessage.ofContentInfo(): String? {
    return notification?.body
}

fun RemoteMessage.ofTitle(): String? {
    return notification?.title
}

fun RemoteMessage.ofTime(): Long? {
    return notification?.eventTime
}

fun RemoteMessage.ofSmallIcon(context: Context): Int? {
    val iconResName = notification?.icon ?: return null
    return context.resources.getIdentifier(iconResName, "int", context.packageName)
}

fun RemoteMessage.ofId(): Int? {
    return notification?.tag?.hashCode()
}

fun RemoteMessage.ofAutoCancel(): Boolean? {
    return notification?.sticky
}

fun RemoteMessage.ofContentIntent(): PendingIntentBuilder? {
    val intentAction = notification?.clickAction ?: return null

    val pendingIntentBuilder = PendingIntentBuilder()
    pendingIntentBuilder.targetIntent { From.ACTIVITY }
    pendingIntentBuilder.taskStackElements {
        listOf(taskStackElement { intent { intentAction { intentAction } } })
    }

    return pendingIntentBuilder
}

fun RemoteMessage.wrap(applicationContext: Context): Notification {
    return notification {
        alarm {
            ledLight { ofLEDLight() }
            sound { ofSound() }
            vibrate { ofVibrateTimings() }
        }
        channel {
            channelInfo { ofChannelInfo() }
            importance { ofImportance() }
        }
        content {
            info { ofContentInfo() }
            time { ofTime() }
            title { ofTitle() }
        }
        icons {
            smallIcon { ofSmallIcon(applicationContext) }
        }
        identifier {
            id { ofId() }
        }
        intention {
            autoCancel { ofAutoCancel() }
            // FIXME: 1/13/20 dirty hack
            ofContentIntent()?.apply {
                this@intention.contentIntent { this }
            }
        }
    }
}