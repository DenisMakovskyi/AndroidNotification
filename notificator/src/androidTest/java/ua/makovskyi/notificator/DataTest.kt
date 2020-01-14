package ua.makovskyi.notificator

import android.graphics.Color

import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

import ua.makovskyi.notificator.data.LEDLight
import ua.makovskyi.notificator.data.notificationAlarm
import ua.makovskyi.notificator.utils.defaultNotificationSound

@RunWith(AndroidJUnit4::class)
class DataTest {

    @Test
    fun alarmTest() {
        val sound = defaultNotificationSound()
        val vibrate = longArrayOf(500L, 500L, 500L, 500L)
        val ledLight = LEDLight(Color.BLUE, 100, 100)

        val alarm = notificationAlarm {
            sound { defaultNotificationSound() }
            vibrate { longArrayOf(500L, 500L, 500L, 500L) }
            ledLight { LEDLight(Color.BLUE, 100, 100) }
        }

        assertEquals(sound, alarm.sound)
        assertArrayEquals(vibrate, alarm.vibrate)
        assertEquals(ledLight, alarm.ledLight)
    }
}