package ua.makovskyi.notificator

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.content.Context
import android.media.AudioAttributes
import android.os.Build

import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import ua.makovskyi.notificator.data.*
import ua.makovskyi.notificator.utils.*

/**
 * @author Denis Makovskyi
 */

object Notificator {

    fun showNotification(context: Context, notification: Notification) {
        NotificationCompat.Builder(context, notification.channel.channelInfo.channelId).apply {
            // - alarm
            notification.alarm.sound.safe { uri ->
                setSound(uri)
            }
            notification.alarm.vibrate.safe { pattern ->
                setVibrate(pattern)
            }
            notification.alarm.ledLight.safe { led ->
                setLights(led.argb, led.onMs, led.offMs)
            }
            // - icons
            notification.icons.safe { icons ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setBadgeIconType(icons.badgeType)
                }
                if (icons.smallIcon > 0) {
                    setSmallIcon(icons.smallIcon)
                }
                if (icons.smallTint > 0) {
                    color = ContextCompat.getColor(context, icons.smallTint)
                }
                icons.largeIcon.safe { icon ->
                    setLargeIcon(icon)
                }
            }
            // - content
            notification.content.time.safe { time ->
                setWhen(time)
                setShowWhen(true)
            }
            val textStyle = NotificationCompat.BigTextStyle()
            notification.content.info.safe { info ->
                setContentInfo(info)
                textStyle.setSummaryText(info)
            }
            notification.content.title.safe { title ->
                setContentTitle(title)
                textStyle.setBigContentTitle(title)
            }
            notification.content.message.safe { message ->
                setContentText(message)
                textStyle.bigText(message)
            }
            setStyle(textStyle)
            // - intention
            setAutoCancel(notification.intention.autoCancel)
            notification.intention.deleteIntent.safe { intent ->
                setDeleteIntent(intent)
            }
            notification.intention.contentIntent.safe { intent ->
                setContentIntent(intent)
            }
            // - identifier
            notification.identifier.groupKey.safe { groupKey ->
                setGroup(groupKey)
                setGroupSummary(true)
                notification.identifier.sortKey.safe { sortKey ->
                    setSortKey(sortKey)
                }
            }
            // - priority before Oreo
            priority = notification.channel.importance.priority

        }.only { builder ->
            NotificationManagerCompat.from(context).apply {
                    // - channel and channel group
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // - channel
                        if (getNotificationChannel(notification.channel.channelInfo.channelId) == null) {
                            createNotificationChannel(createChannel(notification.channel, notification.alarm))
                        }
                        // - group
                        notification.channel.groupingParams.safe { groupingParams ->
                            if (groupingParams.groupId != null && getNotificationChannelGroup(groupingParams.groupId) == null) {
                                createChannelGroup(groupingParams).safe { group ->
                                    createNotificationChannelGroup(group)
                                }
                            }
                        }
                    }
                }
                .safe { manager ->
                    manager.notify(notification.identifier.id, builder.build())
                }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createChannel(channel: Channel, alarm: Alarm): NotificationChannel =
        NotificationChannel(
            channel.channelInfo.channelId,
            channel.channelInfo.channelName,
            channel.importance.importance // importance after Oreo
        ).apply {
            channel.channelInfo.channelDescription.safe { channelDescription ->
                description = channelDescription
            }
            channel.groupingParams?.groupId.safe { groupId ->
                group = groupId
            }
            // - sound
            alarm.sound.safe { uri ->
                setSound(uri, AudioAttributes.Builder()
                    .apply {
                        setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            setAllowedCapturePolicy(alarm.capturePolicy.policy)
                        }
                    }.build())
            }
            // - vibration
            alarm.vibrate.safe { pattern ->
                enableVibration(true)
                vibrationPattern = pattern

            }
            // - led indicator
            alarm.ledLight.safe { led ->
                enableLights(true)
                lightColor = led.argb
            }
        }

    @TargetApi(Build.VERSION_CODES.O)
    fun createChannelGroup(groupingParams: GroupingParams): NotificationChannelGroup? =
        if (groupingParams.groupId != null && groupingParams.groupName != null) {
            NotificationChannelGroup(groupingParams.groupId, groupingParams.groupName).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    groupingParams.groupDescription.safe { groupDescription ->
                        description = groupDescription
                    }
                }
            }
        } else {
            null
        }
}