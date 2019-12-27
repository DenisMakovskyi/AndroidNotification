package ua.makovskyi.notificator.data

import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

class Content private constructor(
    val info: String?,
    val title: String?,
    val message: String?
) {

    @NotificationMarker
    class Builder(
        var info: String? = null,
        var title: String? = null,
        var message: String? = null
    ) {

        internal fun build(init: Builder.() -> Unit): Content {
            init()
            return build()
        }

        internal fun build(): Content = Content(info, title, message)
    }
}

fun notificationContent(init: Content.Builder.() -> Unit): Content = Content.Builder().build(init)