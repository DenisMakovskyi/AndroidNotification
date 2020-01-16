package ua.makovskyi.notificator.data

import android.app.PendingIntent
import android.graphics.Bitmap

import androidx.annotation.RestrictTo
import androidx.core.app.NotificationCompat

import ua.makovskyi.notificator.dsl.ContentMarker
import ua.makovskyi.notificator.dsl.NotificationMarker
import ua.makovskyi.notificator.dsl.SemanticMarker

/**
 * @author Denis Makovskyi
 */

/**
 * Notification style behaviour.
 *
 * Determines whether the notification content will be taken from [Content] as default or from [ContentStyle].
 *
 * @property IGNORE - will be taken from content.
 * @property OVERRIDE - will be taken from style.
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

/**
 * Notification style.
 *
 * May be implemented as a [TextStyle] or [ImageStyle].
 *
 * @param behaviour - notification style behaviour. Default field for all [ContentStyle] descendants. [StyleBehaviour.IGNORE] by default.
 */
sealed class ContentStyle(internal val behaviour: StyleBehaviour) {

    /**
     * Stub. Using as a default implementation in [Content.contentStyle].
     */
    object NOTHING: ContentStyle(StyleBehaviour.IGNORE)

    /**
     * Notification style for large(expandable) text displaying.
     *
     * @param info - hint after application name. If [ContentStyle.behaviour] is [StyleBehaviour.OVERRIDE] for this style,
     * even in collapsed notification, info will be taken from [TextStyle.info] not from a [Content.info].
     * @param title - first line, shows when notification expanded and replace [Content.title].
     * @param message - second line, shows completely (if long) when notification expanded. If [ContentStyle.behaviour] is [StyleBehaviour.OVERRIDE] for this style,
     * even in collapsed notification, message will be taken from [TextStyle.message] not from a [Content.message].
     */
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

    /**
     * Notification style for icon and image displaying.
     *
     * @param info - text hint. When notification collapsed - info is hidden, otherwise, when expanded - shows below [title].
     * @param title - first line, shows when notification expanded and replace [Content.title].
     * @param largeIcon - icon near right border of notification.
     * @param bigPicture - big picture below the main content, shows when notification expanded and replaces [Content.message].
     */
    class ImageStyle(
        behaviour: StyleBehaviour,
        internal val info: String?,
        internal val title: String?,
        internal val largeIcon: Bitmap?,
        internal var bigPicture: Bitmap?
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

/**
 * Builder for [NotificationCompat.Action] item.
 *
 * @property icon - small icon representing the action.
 * @property title - title of the action.
 * @property actionIntent - intent to send when the user invokes this action. May be null, in which case the action
 * may be rendered in a disabled presentation.
 */
@ContentMarker
@SemanticMarker
class SemanticActionBuilder {

    private var icon: Int = 0
    private var title: String? = null
    private var actionIntent: PendingIntent? = null

    fun icon(init: () -> Int) {
        icon = init()
    }

    fun title(init: () -> String?) {
        title = init()
    }

    fun actionIntent(builder: PendingIntentBuilder) {
        actionIntent = builder.build()
    }

    fun actionIntent(init: PendingIntentBuilder.() -> Unit) {
        actionIntent = PendingIntentBuilder().build(init)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun build(): NotificationCompat.Action = NotificationCompat.Action(icon, title, actionIntent)

    internal fun build(init: SemanticActionBuilder.() -> Unit): NotificationCompat.Action {
        init()
        return build()
    }
}

fun semanticAction(init: SemanticActionBuilder.() -> Unit): NotificationCompat.Action = SemanticActionBuilder().build(init)

class Content private constructor(
    internal val time: Long?,
    internal val info: String?,
    internal val title: String?,
    internal val message: String?,
    internal val largeIcon: Bitmap?,
    internal val contentStyle: ContentStyle,
    internal val semanticActions: List<NotificationCompat.Action>
) {

    @ContentMarker
    @NotificationMarker
    class Builder(
        private var time: Long? = null,
        private var info: String? = null,
        private var title: String? = null,
        private var message: String? = null,
        private var largeIcon: Bitmap? = null,
        private var contentStyle: ContentStyle = ContentStyle.NOTHING,
        private var semanticActions: List<NotificationCompat.Action> = listOf()
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

        fun semanticActions(vararg actions: NotificationCompat.Action) {
            semanticActions = actions.toList()
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun build(): Content = Content(time, info, title, message, largeIcon, contentStyle, semanticActions)

        internal fun build(init: Builder.() -> Unit): Content {
            init()
            return build()
        }
    }
}

fun notificationContent(init: Content.Builder.() -> Unit): Content = Content.Builder().build(init)