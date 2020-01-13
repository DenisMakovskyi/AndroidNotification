package ua.makovskyi.notificator.data

import android.graphics.Bitmap
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat.BADGE_ICON_NONE
import androidx.core.app.NotificationCompat.BadgeIconType
import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

class Icons private constructor(
    internal val badgeType: Int,
    internal val smallIcon: Int,
    internal val smallTint: Int,
    internal val largeIcon: Bitmap?
) {

    @NotificationMarker
    class Builder(
        @BadgeIconType
        private var badgeType: Int = BADGE_ICON_NONE,
        @DrawableRes
        private var smallIcon: Int = 0,
        @ColorRes
        private var smallTint: Int = 0,
        private var largeIcon: Bitmap? = null
    ) {

        fun badgeType(init: () -> Int) {
            badgeType = init()
        }

        fun smallIcon(init: () -> Int?) {
            smallIcon = init() ?: return
        }

        fun smallTint(init: () -> Int) {
            smallTint = init()
        }

        fun largeIcon(init: () -> Bitmap) {
            largeIcon = init()
        }

        internal fun build(init: Builder.() -> Unit): Icons {
            init()
            return build()
        }

        internal fun build(): Icons = Icons(badgeType, smallIcon, smallTint, largeIcon)
    }
}

fun notificationIcons(init: Icons.Builder.() -> Unit): Icons = Icons.Builder().build(init)