package ua.makovskyi.notificator

import android.graphics.Color

import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

import ua.makovskyi.notificator.data.*
import ua.makovskyi.notificator.utils.defaultNotificationSound

@RunWith(AndroidJUnit4::class)
class DataTest {

    @Test
    fun alarmTest() {

        val alarm = notificationAlarm {
            sound { defaultNotificationSound() }
            vibrate { longArrayOf(500L, 500L, 500L, 500L) }
            ledLight { LEDLight(Color.BLUE, 100, 100) }
        }

        val notification = notification {
            alarm(alarm) {
                ledLight { LEDLight(Color.RED, 200, 200) }
            }
        }

        assertArrayEquals(notification.alarm.vibrate, longArrayOf(500L, 500L, 500L, 500L))
        assertNotEquals(notification.alarm.ledLight?.argb, Color.BLUE)
    }
}