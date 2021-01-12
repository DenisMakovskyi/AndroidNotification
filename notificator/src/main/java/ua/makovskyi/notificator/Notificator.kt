package ua.makovskyi.notificator

import android.os.Build
import android.content.Context
import android.media.AudioAttributes
import android.app.NotificationChannel
import android.app.NotificationChannelGroup

import androidx.annotation.RequiresApi
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
        val notificationManager = NotificationManagerCompat.from(context)
        val androidNotification = buildAndroidNotification(context, notification)
        if (isApiLevel(Build.VERSION_CODES.O)) {
            createNotificationChannel(
                context, notification.channel, notification.alarm
            )
        }
        notificationManager.notify(
            notification.identifier.id, androidNotification
        )
    }

    fun buildAndroidNotification(
        context: Context,
        notification: Notification
    ): android.app.Notification {
        return NotificationCompat.Builder(context, notification.channel.channelInfo.channelId)
            .also { builder ->
                // - alarm
                builder.setSound(notification.alarm.sound)
                builder.setVibrate(notification.alarm.vibrate)
                notification.alarm.ledLight.safe { led ->
                    builder.setLights(led.argb, led.onMs, led.offMs)
                }
                // - icons
                notification.icons.only { icons ->
                    if (isApiLevel(Build.VERSION_CODES.O)) {
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
                val style = when (notification.content.contentStyle) {
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
                with(notification.identifier) {
                    builder.setOngoing(ongoing)
                    groupKey.safe { groupKey ->
                        builder.setGroup(groupKey)
                        builder.setGroupSummary(true)
                        sortKey.safe { sortKey ->
                            builder.setSortKey(sortKey)
                        }
                    }
                    category.safe { category ->
                        builder.setCategory(category)
                    }
                }
                // - priority before Oreo
                builder.priority = notification.channel.importance.priority
            }.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context, channel: Channel, alarm: Alarm) {
        val notificationChannel = NotificationChannel(
            channel.channelInfo.channelId,
            channel.channelInfo.channelName,
            channel.importance.importance // importance after Oreo
        ).also { androidChannel ->
            // - visibility on lock screen
            androidChannel.lockscreenVisibility = channel.visibility
            // - description info and grouping
            channel.channelInfo.channelDescription.safe { channelDescription ->
                androidChannel.description = channelDescription
            }
            channel.groupingParams?.groupId.safe { groupId ->
                androidChannel.group = groupId
            }
            // - sound
            alarm.sound.safe { uri ->
                androidChannel.setSound(
                    uri, AudioAttributes.Builder()
                        .also { attrs ->
                            attrs.setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            attrs.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            if (isApiLevel(Build.VERSION_CODES.Q)) {
                                attrs.setAllowedCapturePolicy(alarm.capturePolicy.policy)
                            }
                        }.build()
                )
            }
            // - vibration
            alarm.vibrate.safe { pattern ->
                androidChannel.enableVibration(true)
                androidChannel.vibrationPattern = pattern

            }
            // - led indicator
            alarm.ledLight.safe { led ->
                androidChannel.enableLights(true)
                androidChannel.lightColor = led.argb
            }
        }
        NotificationManagerCompat.from(context).also { manager ->
            // - channel and channel group
            if (isApiLevel(Build.VERSION_CODES.O)) {
                // - channel
                if (manager.getNotificationChannel(channel.channelInfo.channelId) == null) {
                    manager.createNotificationChannel(notificationChannel)
                }
                // - group
                channel.groupingParams.safe { groupingParams ->
                    if (
                        groupingParams.groupId != null
                        && manager.getNotificationChannelGroup(groupingParams.groupId) == null
                    ) {
                        createNotificationChannelGroup(groupingParams).safe { group ->
                            manager.createNotificationChannelGroup(group)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannelGroup(groupingParams: GroupingParams): NotificationChannelGroup? {
        return if (groupingParams.groupId != null && groupingParams.groupName != null) {
            NotificationChannelGroup(
                groupingParams.groupId,
                groupingParams.groupName
            ).also { group ->
                if (isApiLevel(Build.VERSION_CODES.P)) {
                    groupingParams.groupDescription.safe { groupDescription ->
                        group.description = groupDescription
                    }
                }
            }
        } else null
    }
}