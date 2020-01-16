package ua.makovskyi.androidnotification

import java.util.concurrent.TimeUnit

import kotlinx.android.synthetic.main.activity_main.*

import android.app.PendingIntent
import android.content.Intent
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
                message { "Notification with expandable message." }
                largeIcon { bitmapFromResources(applicationContext, R.drawable.pic_android_large) }
                withTextStyle {
                    behaviour { StyleBehaviour.OVERRIDE }
                    info { "Big text info" }
                    title { "Big text title" }
                    message { "Notification with very very long expandable message which will be displayed in BigTextStyle." }
                }
                semanticActions(
                    semanticAction {
                        title { "Click me" }
                        actionIntent {
                            pendingFlags(PendingIntent.FLAG_ONE_SHOT)
                            targetIntent { From.ACTIVITY }
                            packageContext { applicationContext }
                            taskStackElements(
                                taskStackElement {
                                    howPut { HowPut.ONLY_NEXT_INTENT }
                                    intent {
                                        from { ConstructFrom.COMPONENT_NAME }
                                        context { applicationContext }
                                        targetClass { MainActivity::class.java }
                                        intentBehaviour(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                }
                            )
                        }
                    }
                )
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
                message { "Notification with picture." }
                largeIcon { bitmapFromResources(applicationContext, R.drawable.pic_android_large) }
                withImageStyle {
                    behaviour { StyleBehaviour.OVERRIDE }
                    info { "Big image info" }
                    title { "Big image title" }
                    largeIcon { bitmapFromResources(applicationContext, R.drawable.pic_android_large) }
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
