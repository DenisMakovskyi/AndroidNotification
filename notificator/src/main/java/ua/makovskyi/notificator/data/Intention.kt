package ua.makovskyi.notificator.data

import android.app.PendingIntent
import android.content.Context

import androidx.core.app.TaskStackBuilder

import ua.makovskyi.notificator.dsl.NotificationMarker
import ua.makovskyi.notificator.dsl.PendingIntentMarker
import ua.makovskyi.notificator.utils.buildMessage
import ua.makovskyi.notificator.utils.isSingle
import ua.makovskyi.notificator.utils.safe

/**
 * @author Denis Makovskyi
 */

enum class From {
    SERVICE,
    ACTIVITY,
    BROADCAST
}

@PendingIntentMarker
class PendingIntentBuilder {

    var from: From? = null
    var context: Context? = null
    var requestCode: Int = 100
    //-
    var taskElements: List<TaskElement> = listOf()
    var behaviourFlags: Int = PendingIntent.FLAG_UPDATE_CURRENT

    internal fun build(init: PendingIntentBuilder.() -> Unit): PendingIntent {
        init()
        require(taskElements.isNotEmpty()) {
            buildMessage(
                PendingIntentBuilder::class,
                "Can not create pending intent from empty tasks list")
        }
        val ctx = requireNotNull(context) {
            buildMessage(
                PendingIntentBuilder::class,
                "To create pending intent, please pass your package context")
        }
        return when(from) {
            From.SERVICE -> {
                val intent = taskElements.first().intent
                if (intent != null) {
                    PendingIntent.getService(ctx, requestCode, intent, behaviourFlags)
                } else {
                    throw IllegalArgumentException(
                        buildMessage(
                            PendingIntentBuilder::class,
                            "Can not create pending intent from empty intent"))
                }
            }
            From.ACTIVITY -> {
                if (taskElements.isSingle()) {
                    taskElements.first().intent.let { intent ->
                        PendingIntent.getActivity(ctx, requestCode, intent, behaviourFlags)
                    }

                } else {
                    requireNotNull(TaskStackBuilder.create(ctx).run {
                        for (element in taskElements) {
                            element.intent.safe { intent ->
                                when(element.howPut) {
                                    HowPut.ONLY_NEXT_INTENT -> addNextIntent(intent)
                                    HowPut.ONLY_EXTRACT_PARENT -> addParentStack(intent.component)
                                    HowPut.NEXT_INTENT_WITH_PARENT -> addNextIntentWithParentStack(intent)
                                }
                            }
                        }
                        getPendingIntent(requestCode, behaviourFlags)
                    })
                }
            }
            From.BROADCAST -> {
                val intent = taskElements.first().intent
                if (intent != null) {
                    PendingIntent.getBroadcast(ctx, requestCode, intent, behaviourFlags)
                } else {
                    throw IllegalArgumentException(
                        buildMessage(
                            PendingIntentBuilder::class,
                            "Can not create pending intent from empty intent"))
                }
            }
            else -> {
                throw IllegalArgumentException(
                    buildMessage(
                        PendingIntentBuilder::class,
                        "Can not create pending intent from undefined target"))
            }
        }
    }
}

class Intention private constructor(
    val autoCancel: Boolean,
    val pendingIntent: PendingIntent?
) {

    @NotificationMarker
    @PendingIntentMarker
    class Builder(
        var autoCancel: Boolean = true,
        var pendingIntent: PendingIntent? = null
    ) {

        fun contentIntent(init: PendingIntentBuilder.() -> Unit) {
            pendingIntent = PendingIntentBuilder().build(init)
        }

        internal fun build(init: Builder.() -> Unit): Intention {
            init()
            return build()
        }

        internal fun build(): Intention = Intention(autoCancel, pendingIntent)
    }
}

fun notificationIntention(init: Intention.Builder.() -> Unit): Intention = Intention.Builder().build(init)