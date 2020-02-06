package ua.makovskyi.notificator.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RestrictTo
import androidx.core.app.NotificationCompat.BADGE_ICON_NONE
import androidx.core.app.NotificationCompat.BadgeIconType

import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

data class Icons constructor(
    val badgeType: Int,
    val smallIcon: Int,
    val smallTint: Int
) {

    @NotificationMarker
    class Builder(
        @BadgeIconType
        private var badgeType: Int = BADGE_ICON_NONE,
        @DrawableRes
        private var smallIcon: Int = 0,
        @ColorRes
        private var smallTint: Int = 0
    ) {

        constructor(icons: Icons) : this(
            icons.badgeType,
            icons.smallIcon,
            icons.smallTint)

        fun badgeType(init: () -> Int) {
            badgeType = init()
        }

        fun smallIcon(init: () -> Int) {
            smallIcon = init()
        }

        fun smallTint(init: () -> Int) {
            smallTint = init()
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun build(): Icons = Icons(badgeType, smallIcon, smallTint)

        internal fun build(init: Builder.() -> Unit): Icons {
            init()
            return build()
        }
    }
}

fun notificationIcons(init: Icons.Builder.() -> Unit): Icons = Icons.Builder().build(init)