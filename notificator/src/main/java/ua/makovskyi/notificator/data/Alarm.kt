package ua.makovskyi.notificator.data

import java.util.concurrent.TimeUnit

import android.net.Uri
import android.graphics.Color

import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

data class LEDLight(
    internal val argb: Int = Color.BLUE,
    internal val onMs: Int = TimeUnit.SECONDS.toMillis(1).toInt(),
    internal val offMs: Int = TimeUnit.SECONDS.toMillis(1).toInt())

class Alarm private constructor(
    internal val sound: Uri?,
    internal val vibrate: LongArray?,
    internal val ledLight: LEDLight?
) {

    @NotificationMarker
    class Builder(
        private var sound: Uri? = null,
        private var vibrate: LongArray? = null,
        private var ledLight: LEDLight? = null
    ) {

        fun sound(init: () -> Uri) {
            sound = init()
        }

        fun vibrate(init: () -> LongArray) {
            vibrate = init()
        }

        fun ledLight(init: () -> LEDLight) {
            ledLight = init()
        }

        internal fun build(init: Builder.() -> Unit): Alarm {
            init()
            return build()
        }

        internal fun build(): Alarm = Alarm(sound, vibrate, ledLight)
    }
}

fun notificationAlarm(init: Alarm.Builder.() -> Unit): Alarm = Alarm.Builder().build(init)