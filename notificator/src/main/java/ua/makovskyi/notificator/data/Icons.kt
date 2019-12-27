package ua.makovskyi.notificator.data

import android.graphics.Bitmap
import android.app.Notification

import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

class Icons private constructor(
    val badgeType: Int,
    val smallIcon: Int,
    val smallTint: Int,
    val largeIcon: Bitmap?
) {

    @NotificationMarker
    class Builder(
        var badgeType: Int = Notification.BADGE_ICON_NONE,
        var smallIcon: Int = 0,
        var smallTint: Int = 0,
        var largeIcon: Bitmap? = null
    ) {

        internal fun build(init: Builder.() -> Unit): Icons {
            init()
            return build()
        }

        internal fun build(): Icons = Icons(badgeType, smallIcon, smallTint, largeIcon)
    }
}

fun notificationIcons(init: Icons.Builder.() -> Unit): Icons = Icons.Builder().build(init)