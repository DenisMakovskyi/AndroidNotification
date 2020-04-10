package ua.makovskyi.notificator.data

import android.content.Context

import ua.makovskyi.notificator.Notificator
import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

data class Notification constructor(
    val alarm: Alarm,
    val icons: Icons,
    val content: Content,
    val channel: Channel,
    val intention: Intention,
    val identifier: Identifier
) {

    @NotificationMarker
    class Builder(
        var alarm: Alarm = Alarm.Builder().build(),
        var icons: Icons = Icons.Builder().build(),
        var content: Content = Content.Builder().build(),
        var channel: Channel = Channel.Builder().build(),
        var intention: Intention = Intention.Builder().build(),
        var identifier: Identifier = Identifier.Builder().build()
    ) {

        constructor (notification: Notification) : this(
            notification.alarm,
            notification.icons,
            notification.content,
            notification.channel,
            notification.intention,
            notification.identifier)

        fun alarm(init: Alarm.Builder.() -> Unit) {
            alarm = Alarm.Builder().build(init)
        }

        fun alarm(notificationAlarm: Alarm, init: Alarm.Builder.() -> Unit) {
            alarm = Alarm.Builder(notificationAlarm).build(init)
        }

        fun icons(init: Icons.Builder.() -> Unit) {
            icons = Icons.Builder().build(init)
        }

        fun icons(notificationIcons: Icons, init: Icons.Builder.() -> Unit) {
            icons = Icons.Builder(notificationIcons).build(init)
        }

        fun content(init: Content.Builder.() -> Unit) {
            content = Content.Builder().build(init)
        }

        fun content(notificationContent: Content, init: Content.Builder.() -> Unit) {
            content = Content.Builder(notificationContent).build(init)
        }

        fun channel(init: Channel.Builder.() -> Unit) {
            channel = Channel.Builder().build(init)
        }

        fun channel(notificationChannel: Channel, init: Channel.Builder.() -> Unit) {
            channel = Channel.Builder(notificationChannel).build(init)
        }

        fun intention(init: Intention.Builder.() -> Unit) {
            intention = Intention.Builder().build(init)
        }

        fun intention(notificationIntention: Intention, init: Intention.Builder.() -> Unit) {
            intention = Intention.Builder(notificationIntention).build(init)
        }

        fun identifier(init: Identifier.Builder.() -> Unit) {
            identifier = Identifier.Builder().build(init)
        }

        fun identifier(notificationIdentifier: Identifier, init: Identifier.Builder.() -> Unit) {
            identifier = Identifier.Builder(notificationIdentifier).build(init)
        }

        internal fun build(init: Builder.() -> Unit): Notification {
            init()
            return Notification(alarm, icons, content, channel, intention, identifier)
        }
    }

    fun show(context: Context) {
        Notificator.showNotification(context, this)
    }

    fun build(context: Context): android.app.Notification {
        return Notificator.buildNotification(context, this)
    }

    fun createChannel(context: Context) {
        Notificator.createChannel(channel, alarm)
    }
}

fun notification(init: Notification.Builder.() -> Unit): Notification = Notification.Builder().build(init)

fun notification(notification: Notification, init: Notification.Builder.() -> Unit): Notification = Notification.Builder(notification).build(init)

