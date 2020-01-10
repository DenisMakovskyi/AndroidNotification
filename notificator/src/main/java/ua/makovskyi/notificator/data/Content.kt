package ua.makovskyi.notificator.data

import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

class Content private constructor(
    internal val time: Long?,
    internal val info: String?,
    internal val title: String?,
    internal val message: String?
) {

    @NotificationMarker
    class Builder(
        private var time: Long? = null,
        private var info: String? = null,
        private var title: String? = null,
        private var message: String? = null
    ) {

        fun time(init: () -> Long) {
            time = init()
        }

        fun info(init: () -> String) {
            info = init()
        }

        fun title(init: () -> String) {
            title = init()
        }

        fun message(init: () -> String) {
            message = init()
        }

        internal fun build(init: Builder.() -> Unit): Content {
            init()
            return build()
        }

        internal fun build(): Content = Content(time, info, title, message)
    }
}

fun notificationContent(init: Content.Builder.() -> Unit): Content = Content.Builder().build(init)