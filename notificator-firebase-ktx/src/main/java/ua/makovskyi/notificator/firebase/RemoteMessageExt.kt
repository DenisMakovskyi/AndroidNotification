package ua.makovskyi.notificator.firebase

import kotlinx.coroutines.runBlocking

import android.app.NotificationManager
import android.content.Context
import android.net.Uri

import coil.Coil
import coil.api.get

import com.google.firebase.messaging.RemoteMessage

import ua.makovskyi.notificator.data.*
import ua.makovskyi.notificator.utils.toBundle

/**
 * Created by azazellj on 1/11/20.
 */

fun RemoteMessage.wrap(applicationContext: Context): Notification {
    return notification {
        alarm {
            ofSound()?.let { sound { it } }
            ofVibrate()?.let { vibrate { it } }
            ofLEDLight()?.let { ledLight { it } }
        }
        icons {
            smallIcon { ofSmallIcon(applicationContext) }
        }
        content {
            ofTime()?.let { time { it } }
            ofTitle(applicationContext)?.let { title { it } }
            ofMessage(applicationContext)?.let { message { it } }
            ofPicture()?.let {
                withImageStyle {
                    behaviour { StyleBehaviour.IGNORE }
                    bigPicture {
                        runBlocking {
                            drawableToBitmap(Coil.get(it))
                        }
                    }
                }
            }
        }
        channel {
            importance { ofImportance() }
            channelInfo {
                ofChannelId()?.let { channelId { it } }
            }
        }
        intention {
            autoCancel { ofAutoCancel() }
            ofContentIntent(applicationContext)?.let { contentIntent(it) }
        }
        identifier {
            id { ofId() }
        }
    }
}

private fun RemoteMessage.ofSound(): Uri? {
    if (notification?.defaultSound == true) return null
    return notification?.sound?.let { Uri.parse(it) }
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

private fun RemoteMessage.ofMessage(context: Context): String? {
    val bodyLocKey = notification?.bodyLocalizationKey

    if (bodyLocKey.isNullOrEmpty()) return notification?.body

    return stringFromResources(context, bodyLocKey).also { body ->
        val bodyLocArgs = notification?.bodyLocalizationArgs
        if (!bodyLocArgs.isNullOrEmpty()) String.format(body, bodyLocArgs)
    }
}

private fun RemoteMessage.ofPicture(): String? {
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

private fun RemoteMessage.ofChannelId(): String? {
    return notification?.channelId
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