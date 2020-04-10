package ua.makovskyi.notificator

import android.os.Build
import android.content.Context
import android.annotation.TargetApi
import android.media.AudioAttributes
import android.app.NotificationChannel
import android.app.NotificationChannelGroup

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
        val androidNotification = buildNotification(context, notification)
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
        }.safe { manager ->
            manager.notify(notification.identifier.id, androidNotification)
        }
    }

    fun buildNotification(context: Context, notification: Notification): android.app.Notification {
        return NotificationCompat.Builder(context, notification.channel.channelInfo.channelId).also { builder ->
            // - alarm
            builder.setSound(notification.alarm.sound)
            builder.setVibrate(notification.alarm.vibrate)
            notification.alarm.ledLight.safe { led ->
                builder.setLights(led.argb, led.onMs, led.offMs)
            }
            // - icons
            notification.icons.only { icons ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setBadgeIconType(icons.badgeType)
                }
                if (icons.smallIcon != 0) {
                    builder.setSmallIcon(icons.smallIcon)
                }
                if (icons.smallTint != 0) {
                    builder.color = ContextCompat.getColor(context, icons.smallTint)
                }
            }
            // - content
            if (notification.content.color != 0) {
                builder.color = notification.content.color
                builder.setColorized(true)
            }
            notification.content.time.safe { time ->
                builder.setWhen(time)
                builder.setShowWhen(true)
            }
            builder.setContentInfo(notification.content.info)
            builder.setContentTitle(notification.content.title)
            builder.setContentText(notification.content.plainText)
            builder.setLargeIcon(notification.content.largeIcon)
            // - content style
            val style = when(notification.content.contentStyle) {
                is ContentStyle.TextStyle -> {
                    NotificationCompat.BigTextStyle().also { textStyle ->
                        textStyle.bigText(notification.content.contentStyle.bigText)
                        textStyle.setSummaryText(notification.content.contentStyle.summary)
                        if (notification.content.contentStyle.override()) {
                            textStyle.setBigContentTitle(notification.content.contentStyle.title)
                        }
                    }
                }
                is ContentStyle.ImageStyle -> {
                    NotificationCompat.BigPictureStyle().also { pictureStyle ->
                        pictureStyle.bigPicture(notification.content.contentStyle.bigPicture)
                        pictureStyle.setSummaryText(notification.content.contentStyle.summary)
                        if (notification.content.contentStyle.override()) {
                            pictureStyle.bigLargeIcon(notification.content.contentStyle.largeIcon)
                            pictureStyle.setBigContentTitle(notification.content.contentStyle.title)
                        }
                    }
                }
                is ContentStyle.NOTHING -> null
            }
            if (style != null) builder.setStyle(style)
            // - actions
            for (action in notification.content.semanticActions) {
                builder.addAction(action)
            }
            // - intention
            builder.setAutoCancel(notification.intention.autoCancel)
            builder.setDeleteIntent(notification.intention.deleteIntent)
            builder.setContentIntent(notification.intention.contentIntent)
            // - identifier
            notification.identifier.groupKey.safe { groupKey ->
                builder.setGroup(groupKey)
                builder.setGroupSummary(true)
                notification.identifier.sortKey.safe { sortKey ->
                    builder.setSortKey(sortKey)
                }
            }
            // - priority before Oreo
            builder.priority = notification.channel.importance.priority
        }.build()
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createChannel(channel: Channel, alarm: Alarm): NotificationChannel {
        return NotificationChannel(
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
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createChannelGroup(groupingParams: GroupingParams): NotificationChannelGroup? {
        return if (groupingParams.groupId != null && groupingParams.groupName != null) {
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
}