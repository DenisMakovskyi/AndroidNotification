package ua.makovskyi.notificator.data

import java.util.concurrent.TimeUnit

import android.net.Uri
import android.graphics.Color

import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

data class LEDLight(
    val argb: Int = Color.BLUE,
    val onMs: Int = TimeUnit.SECONDS.toMillis(1).toInt(),
    val offMs: Int = TimeUnit.SECONDS.toMillis(1).toInt())

class Alarm private constructor(
    val sound: Uri?,
    val vibrate: LongArray?,
    val ledLight: LEDLight?
) {

    @NotificationMarker
    class Builder(
        var sound: Uri? = null,
        var vibrate: LongArray? = null,
        var ledLight: LEDLight? = null
    ) {

        internal fun build(init: Builder.() -> Unit): Alarm {
            init()
            return build()
        }

        internal fun build(): Alarm = Alarm(sound, vibrate, ledLight)
    }
}

fun notificationAlarm(init: Alarm.Builder.() -> Unit): Alarm = Alarm.Builder().build(init)