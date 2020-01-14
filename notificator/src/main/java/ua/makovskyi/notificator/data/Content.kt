package ua.makovskyi.notificator.data

import android.graphics.Bitmap

import androidx.annotation.RestrictTo

import ua.makovskyi.notificator.dsl.ContentMarker
import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

enum class StyleBehaviour {
    IGNORE,
    OVERRIDE
}

open class DefaultStyleBuilder {

    protected var behaviour: StyleBehaviour = StyleBehaviour.IGNORE

    fun behaviour(init: () -> StyleBehaviour?) {
        behaviour = init() ?: return
    }
}

sealed class ContentStyle(internal val behaviour: StyleBehaviour) {

    object NOTHING: ContentStyle(StyleBehaviour.IGNORE)

    class TextStyle(
        behaviour: StyleBehaviour,
        internal val info: String?,
        internal val title: String?,
        internal val message: String?
    ): ContentStyle(behaviour) {

        @ContentMarker
        class Builder: DefaultStyleBuilder() {
            private var info: String? = null
            private var title: String? = null
            private var message: String? = null

            fun info(init: () -> String?) {
                info = init()
            }

            fun title(init: () -> String?) {
                title = init()
            }

            fun message(init: () -> String?) {
                message = init()
            }

            @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
            fun build(): TextStyle = TextStyle(behaviour, info, title, message)

            internal fun build(init: Builder.() -> Unit): TextStyle {
                init()
                return build()
            }
        }
    }

    class ImageStyle(
        behaviour: StyleBehaviour,
        internal val info: String?,
        internal val title: String?,
        internal val largeIcon: Bitmap?,
        internal var bigPicture: Bitmap? = null
    ): ContentStyle(behaviour) {

        @ContentMarker
        class Builder: DefaultStyleBuilder() {
            private var info: String? = null
            private var title: String? = null
            private var largeIcon: Bitmap? = null
            private var bigPicture: Bitmap? = null

            fun info(init: () -> String?) {
                info = init()
            }

            fun title(init: () -> String?) {
                title = init()
            }

            fun largeIcon(init: () -> Bitmap?) {
                largeIcon = init()
            }

            fun bigPicture(init: () -> Bitmap?) {
                bigPicture = init()
            }

            @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
            fun build(): ImageStyle = ImageStyle(behaviour, info, title, largeIcon, bigPicture)

            internal fun build(init: Builder.() -> Unit): ImageStyle {
                init()
                return build()
            }
        }
    }
}

class Content private constructor(
    internal val time: Long?,
    internal val info: String?,
    internal val title: String?,
    internal val message: String?,
    internal val largeIcon: Bitmap?,
    internal val contentStyle: ContentStyle
) {

    @ContentMarker
    @NotificationMarker
    class Builder(
        private var time: Long? = null,
        private var info: String? = null,
        private var title: String? = null,
        private var message: String? = null,
        private var largeIcon: Bitmap? = null,
        private var contentStyle: ContentStyle = ContentStyle.NOTHING
    ) {

        fun time(init: () -> Long?) {
            time = init()
        }

        fun info(init: () -> String?) {
            info = init()
        }

        fun title(init: () -> String?) {
            title = init()
        }

        fun message(init: () -> String?) {
            message = init()
        }

        fun largeIcon(init: () -> Bitmap?) {
            largeIcon = init()
        }

        fun withTextStyle(builder: ContentStyle.TextStyle.Builder) {
            contentStyle = builder.build()
        }

        fun withTextStyle(init: ContentStyle.TextStyle.Builder.() -> Unit) {
            contentStyle = ContentStyle.TextStyle.Builder().build(init)
        }

        fun withImageStyle(builder: ContentStyle.ImageStyle.Builder) {
            contentStyle = builder.build()
        }

        fun withImageStyle(init: ContentStyle.ImageStyle.Builder.() -> Unit) {
            contentStyle = ContentStyle.ImageStyle.Builder().build(init)
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun build(): Content = Content(time, info, title, message, largeIcon, contentStyle)

        internal fun build(init: Builder.() -> Unit): Content {
            init()
            return build()
        }
    }
}

fun notificationContent(init: Content.Builder.() -> Unit): Content = Content.Builder().build(init)