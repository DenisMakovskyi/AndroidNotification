package ua.makovskyi.notificator.firebase

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import com.google.firebase.messaging.RemoteMessage
import ua.makovskyi.notificator.data.*
import ua.makovskyi.notificator.utils.toBundle


/**
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
        offMs = lightSettings[2])
}

fun RemoteMessage.ofSmallIcon(context: Context): Int {
    val iconResName = notification?.icon ?: return iconFromMetaData(context)
    return context.resources.getIdentifier(iconResName, "int", context.packageName)
}

fun iconFromMetaData(context: Context): Int {
    val info = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)

    val iconRes = info.metaData.getInt("com.google.firebase.messaging.default_notification_icon")
    val appIcon = info.icon

    if (iconRes != 0) return iconRes

    return appIcon
}

fun RemoteMessage.ofTime(): Long? {
    return notification?.eventTime
}

fun RemoteMessage.ofTitle(): String? {
    return notification?.title
}

fun RemoteMessage.ofMessage(): String? {
    return notification?.body
}

fun RemoteMessage.ofImportance(): Importance? {
    val importance = notification?.notificationPriority ?: return null

    return when(importance) {
        NotificationManager.IMPORTANCE_LOW -> Importance.LOW
        NotificationManager.IMPORTANCE_MIN -> Importance.MIN
        NotificationManager.IMPORTANCE_HIGH -> Importance.HIGHT
        NotificationManager.IMPORTANCE_MAX -> Importance.MAXIMAL
        else -> Importance.DEFAULT
    }
}

fun RemoteMessage.ofChannelInfo(): ChannelInfo.Builder? {
    return notification?.channelId?.let { ChannelInfo.Builder(channelId = it) }
}

fun RemoteMessage.ofAutoCancel(): Boolean? {
    return notification?.sticky
}

fun RemoteMessage.ofContentIntent(context: Context): PendingIntentBuilder? {
    val clickAction = notification?.clickAction ?: return null

    return PendingIntentBuilder().also { builder ->
        builder.targetIntent { From.ACTIVITY }
        builder.packageContext { context }
        builder.taskStackElements {
            listOf(
                taskStackElement {
                    intent {
                        from { ConstructFrom.ACTION }
                        intentAction { clickAction }
                        intentExtras { if (data.isNotEmpty()) data.toBundle() else null }
                    }
                }
            )
        }
    }
}

fun RemoteMessage.ofId(): Int? {
    return notification?.tag?.hashCode()
}

fun RemoteMessage.wrap(applicationContext: Context): Notification {
    return notification {
        alarm {
            sound { ofSound() }
            vibrate { ofVibrateTimings() }
            ledLight { ofLEDLight() }
        }
        icons {
            smallIcon { ofSmallIcon(applicationContext) }
        }
        content {
            time { ofTime() }
            title { ofTitle() }
            message { ofMessage() }
        }
        channel {
            importance { ofImportance() }
            channelInfo { ofChannelInfo() }
        }
        val contentIntentBuilder = ofContentIntent(applicationContext)
        if (contentIntentBuilder != null) {
            intention {
                autoCancel { ofAutoCancel() }
                contentIntent(contentIntentBuilder)
            }
        }
        identifier {
            id { ofId() }
        }
    }
}