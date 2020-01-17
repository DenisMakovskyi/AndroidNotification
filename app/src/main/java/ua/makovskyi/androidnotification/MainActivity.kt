package ua.makovskyi.androidnotification

import java.util.concurrent.TimeUnit

import kotlinx.android.synthetic.main.activity_main.*

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import ua.makovskyi.notificator.data.*
import ua.makovskyi.notificator.utils.bitmapFromResources
import ua.makovskyi.notificator.utils.defaultNotificationSound

class MainActivity : AppCompatActivity() {

    private lateinit var alarm: Alarm
    private lateinit var icons: Icons
    private lateinit var channel: Channel
    private lateinit var intention: Intention

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        notify_text_button.setOnClickListener {
            showTextNotification()
        }
        notify_image_button.setOnClickListener {
            showImageNotification()
        }
    }

    private fun init() {
        alarm = notificationAlarm {
            sound { defaultNotificationSound() }
            vibrate { longArrayOf(500L, 500L, 500L, 500L) }
        }
        icons = notificationIcons {
            smallIcon { R.mipmap.ic_launcher }
        }
        channel = notificationChannel {
            importance { Importance.MAXIMAL }
            channelInfo {
                channelId { "${applicationContext.packageName}.default_notification_channel" }
                channelName { "Default channel" }
                channelDescription { "Default notification channel" }
            }
        }
        intention = notificationIntention {
            autoCancel { true }
            contentIntent {
                targetIntent { From.ACTIVITY }
                packageContext { applicationContext }
                taskStackElements(
                    taskStackElement {
                        intent {
                            from { ConstructFrom.ACTION }
                            intentAction { "FIREBASE_MESSAGE" }
                        }
                    }
                )
            }
        }
    }

    private fun showTextNotification() {
        notification {
            alarm = this@MainActivity.alarm
            icons = this@MainActivity.icons
            content {
                time { System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1) }
                info { "Text info" }
                title { "Text title" }
                plainText { "Collapsed message." }
                largeIcon { bitmapFromResources(applicationContext, R.drawable.pic_android_large) }
                withTextStyle {
                    behaviour { StyleBehaviour.OVERRIDE }
                    title { "Expanded text title" }
                    bigText { "Notification with very very long expandable message which will be displayed in BigTextStyle." }
                    summary { "Expanded summary" }
                }
            }
            channel = this@MainActivity.channel
            intention = this@MainActivity.intention
            identifier {
                id { 100 }
            }

        }.show(this)
    }

    private fun showImageNotification() {
        notification {
            alarm = this@MainActivity.alarm
            icons = this@MainActivity.icons
            content {
                info { "Image info" }
                title { "Image title" }
                plainText { "Notification with picture." }
                largeIcon { bitmapFromResources(applicationContext, R.drawable.pic_android_large) }
                withImageStyle {
                    behaviour { StyleBehaviour.IGNORE }
                    //title { "Expanded image title" }
                    summary { "Expanded image summary" }
                    //largeIcon { bitmapFromResources(applicationContext, R.drawable.pic_android_large) }
                    bigPicture { bitmapFromResources(applicationContext, R.drawable.pic_android_big) }
                }
            }
            channel = this@MainActivity.channel
            intention = this@MainActivity.intention
            identifier {
                id { 200 }
            }

        }.show(this)
    }
}
