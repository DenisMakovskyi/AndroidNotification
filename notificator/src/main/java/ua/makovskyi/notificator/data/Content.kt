package ua.makovskyi.notificator.data

import android.app.PendingIntent
import android.graphics.Bitmap

import androidx.annotation.ColorInt
import androidx.annotation.RestrictTo
import androidx.core.app.NotificationCompat

import ua.makovskyi.notificator.dsl.ContentMarker
import ua.makovskyi.notificator.dsl.NotificationMarker
import ua.makovskyi.notificator.dsl.SemanticMarker

/**
 * @author Denis Makovskyi
 */

enum class StyleBehaviour {
    IGNORE,
    OVERRIDE
}

open class DefaultStyleBuilder {

    protected var behaviour = StyleBehaviour.IGNORE

    fun behaviour(init: () -> StyleBehaviour) {
        behaviour = init()
    }
}

sealed class ContentStyle(private val behaviour: StyleBehaviour) {

    object NOTHING : ContentStyle(StyleBehaviour.IGNORE)

    class TextStyle(
        behaviour: StyleBehaviour,
        val title: String?,
        val bigText: String?,
        val summary: String?
    ) : ContentStyle(behaviour) {

        @ContentMarker
        class Builder : DefaultStyleBuilder() {

            private var title: String? = null
            private var bigText: String? = null
            private var summary: String? = null

            fun title(init: () -> String?) {
                title = init()
            }

            fun bigText(init: () -> String?) {
                bigText = init()
            }

            fun summary(init: () -> String?) {
                summary = init()
            }

            @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
            fun build(): TextStyle = TextStyle(behaviour, title, bigText, summary)

            internal fun build(init: Builder.() -> Unit): TextStyle {
                init()
                return build()
            }
        }
    }

    class ImageStyle(
        behaviour: StyleBehaviour,
        val title: String?,
        val summary: String?,
        val largeIcon: Bitmap?,
        var bigPicture: Bitmap?
    ) : ContentStyle(behaviour) {

        @ContentMarker
        class Builder : DefaultStyleBuilder() {
            private var title: String? = null
            private var summary: String? = null
            private var largeIcon: Bitmap? = null
            private var bigPicture: Bitmap? = null

            fun title(init: () -> String?) {
                title = init()
            }

            fun summary(init: () -> String?) {
                summary = init()
            }

            fun largeIcon(init: () -> Bitmap?) {
                largeIcon = init()
            }

            fun bigPicture(init: () -> Bitmap?) {
                bigPicture = init()
            }

            @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
            fun build(): ImageStyle = ImageStyle(behaviour, title, summary, largeIcon, bigPicture)

            internal fun build(init: Builder.() -> Unit): ImageStyle {
                init()
                return build()
            }
        }
    }

    internal fun ignore(): Boolean = behaviour == StyleBehaviour.IGNORE

    internal fun override(): Boolean = behaviour == StyleBehaviour.OVERRIDE
}

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

@Deprecated(
    "Use DSL-style function instead",
    ReplaceWith(
        "Content::semanticAction(init: SemanticActionBuilder.() -> Unit)",
        "ua.makovskyi.notificator.data"
    ),
    DeprecationLevel.WARNING
)
fun semanticAction(init: SemanticActionBuilder.() -> Unit): NotificationCompat.Action = SemanticActionBuilder().build(init)

data class Content(
    val color: Int,
    val time: Long?,
    val info: String?,
    val title: String?,
    val plainText: String?,
    val largeIcon: Bitmap?,
    val contentStyle: ContentStyle,
    val semanticActions: List<NotificationCompat.Action>
) {

    @ContentMarker
    @NotificationMarker
    class Builder(
        @ColorInt
        private var color: Int = 0,
        private var time: Long? = null,
        private var info: String? = null,
        private var title: String? = null,
        private var plainText: String? = null,
        private var largeIcon: Bitmap? = null,
        private var contentStyle: ContentStyle = ContentStyle.NOTHING,
        private var semanticActions: MutableList<NotificationCompat.Action> = mutableListOf()
    ) {

        constructor(content: Content) : this(
            content.color,
            content.time,
            content.info,
            content.title,
            content.plainText,
            content.largeIcon,
            content.contentStyle,
            content.semanticActions.toMutableList()
        )

        operator fun NotificationCompat.Action.unaryPlus() {
            semanticActions.add(this)
        }

        fun color(init: () -> Int) {
            color = init()
        }

        fun time(init: () -> Long?) {
            time = init()
        }

        fun info(init: () -> String?) {
            info = init()
        }

        fun title(init: () -> String?) {
            title = init()
        }

        fun plainText(init: () -> String?) {
            plainText = init()
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

        fun semanticAction(init: SemanticActionBuilder.() -> Unit) {
            +SemanticActionBuilder().apply(init).build()
        }

        @Deprecated(
            "Use DSL-style function instead",
            ReplaceWith(
                "semanticAction(init: SemanticActionBuilder.() -> Unit)",
                "ua.makovskyi.notificator.data"
            ),
            DeprecationLevel.WARNING
        )
        fun semanticActions(vararg actions: NotificationCompat.Action) {
            semanticActions = actions.toMutableList()
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun build(): Content =
            Content(color, time, info, title, plainText, largeIcon, contentStyle, semanticActions)

        internal fun build(init: Builder.() -> Unit): Content {
            init()
            return build()
        }
    }
}

fun notificationContent(init: Content.Builder.() -> Unit): Content = Content.Builder().build(init)