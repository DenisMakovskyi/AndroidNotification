package ua.makovskyi.notificator.data

import android.content.Context

import ua.makovskyi.notificator.Notificator
import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

class Notification private constructor(
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

        fun alarm(init: Alarm.Builder.() -> Unit) {
            alarm = Alarm.Builder().build(init)
        }

        fun icons(init: Icons.Builder.() -> Unit) {
            icons = Icons.Builder().build(init)
        }

        fun content(init: Content.Builder.() -> Unit) {
            content = Content.Builder().build(init)
        }

        fun channel(init: Channel.Builder.() -> Unit) {
            channel = Channel.Builder().build(init)
        }

        fun intention(init: Intention.Builder.() -> Unit) {
            intention = Intention.Builder().build(init)
        }

        fun identifier(init: Identifier.Builder.() -> Unit) {
            identifier = Identifier.Builder().build(init)
        }

        fun build(init: Builder.() -> Unit): Notification {
            init()
            return Notification(alarm, icons, content, channel, intention, identifier)
        }
    }

    fun show(context: Context) = Notificator.showNotification(context, this)
}

fun notification(init: Notification.Builder.() -> Unit): Notification = Notification.Builder().build(init)

