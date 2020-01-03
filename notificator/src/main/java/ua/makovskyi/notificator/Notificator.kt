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
            notification.intention.pendingIntent.safe { intent ->
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
            val manager = context.findSystemService<NotificationManagerCompat>()?.apply {
                // - channel and channel group
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // - channel
                    if (getNotificationChannel(notification.channel.channelInfo.channelId) == null) {
                        createNotificationChannel(createChannel(notification.channel, notification.alarm))
                    }
                    // - group
                    notification.channel.groupingParams?.groupId.safe { groupId ->
                        if (getNotificationChannelGroup(groupId) == null) {
                            createChannelGroup(notification.channel).safe { group ->
                                createNotificationChannelGroup(group)
                            }
                        }
                    }
                }
            }
            manager?.notify(notification.identifier.id, builder.build())
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createChannel(channel: Channel, alarm: Alarm): NotificationChannel =
        NotificationChannel(
            channel.channelInfo.channelId,
            channel.channelInfo.channelName,
            channel.importance.importance
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
                            setAllowedCapturePolicy(AudioAttributes.ALLOW_CAPTURE_BY_ALL)
                        }
                    }.build())
            }
            // - vibration
            alarm.vibrate.safeOr(
                { pattern ->
                    enableVibration(true)
                    vibrationPattern = pattern

                },
                { enableVibration(false) }
            )
            // - led indicator
            alarm.ledLight.safeOr(
                { led ->
                    enableLights(true)
                    lightColor = led.argb
                },
                { enableLights(false) }
            )
        }

    @TargetApi(Build.VERSION_CODES.O)
    fun createChannelGroup(channel: Channel): NotificationChannelGroup? =
        if (channel.groupingParams?.groupId != null && channel.groupingParams.groupName != null) {
            NotificationChannelGroup(channel.groupingParams.groupId, channel.groupingParams.groupName).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    channel.groupingParams.groupDescription.safe { groupDescription ->
                        description = groupDescription
                    }
                }
            }
        } else {
            null
        }
}