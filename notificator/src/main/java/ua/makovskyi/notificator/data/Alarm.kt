package ua.makovskyi.notificator.data

import java.util.concurrent.TimeUnit

import android.net.Uri
import android.graphics.Color
import android.media.AudioAttributes

import androidx.annotation.RestrictTo

import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

/**
 * Device LED indicator parameters.
 *
 * @param argb - color of led indicator.
 * @param onMs - delay in milliseconds while led indicator is glows.
 * @param offMs - delay in milliseconds while led indicator is not glows.
 */
data class LEDLight(
    internal val argb: Int = Color.BLUE,
    internal val onMs: Int = TimeUnit.SECONDS.toMillis(1).toInt(),
    internal val offMs: Int = TimeUnit.SECONDS.toMillis(1).toInt())

/**
 * Notification sound capture policy.
 *
 * Determines for whom notification sound capture ability is enabled.
 *
 * @property ALLOW_BY_ALL - allow for all.
 * @property ALLOW_BY_NONE - allow for none.
 * @property ALLOW_BY_SYSTEM - allow for system.
 */
enum class CapturePolicy(val policy: Int) {
    ALLOW_BY_ALL(AudioAttributes.ALLOW_CAPTURE_BY_ALL),
    ALLOW_BY_NONE(AudioAttributes.ALLOW_CAPTURE_BY_NONE),
    ALLOW_BY_SYSTEM(AudioAttributes.ALLOW_CAPTURE_BY_SYSTEM)
}

/**
 * Notification alarm settings.
 *
 * Includes sound, vibration, LED indicator settings and notification audio capture policy.
 *
 * @param sound - notification sound [Uri]. You can use [ua.makovskyi.notificator.utils.defaultNotificationSound]
 * to obtain device default sound for notifications.
 * @param vibrate - vibration pattern. Example: longArrayOf(500L, 500L, 500L, 500L).
 * @param ledLight - device LED indicator parameters.
 * @param capturePolicy - notification sound capture policy.
 */
class Alarm private constructor(
    internal val sound: Uri?,
    internal val vibrate: LongArray?,
    internal val ledLight: LEDLight?,
    internal val capturePolicy: CapturePolicy
) {

    @NotificationMarker
    class Builder(
        private var sound: Uri? = null,
        private var vibrate: LongArray? = null,
        private var ledLight: LEDLight? = null,
        private var capturePolicy: CapturePolicy = CapturePolicy.ALLOW_BY_ALL
    ) {

        fun sound(init: () -> Uri?) {
            sound = init()
        }

        fun vibrate(init: () -> LongArray?) {
            vibrate = init()
        }

        fun ledLight(init: () -> LEDLight?) {
            ledLight = init()
        }

        fun capturePolicy(init: () -> CapturePolicy?) {
            capturePolicy = init() ?: return
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun build(): Alarm = Alarm(sound, vibrate, ledLight, capturePolicy)

        internal fun build(init: Builder.() -> Unit): Alarm {
            init()
            return build()
        }
    }
}

fun notificationAlarm(init: Alarm.Builder.() -> Unit): Alarm = Alarm.Builder().build(init)