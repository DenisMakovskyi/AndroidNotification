package ua.makovskyi.notificator.data

import android.app.PendingIntent
import android.content.Context

import androidx.annotation.RestrictTo
import androidx.core.app.TaskStackBuilder

import ua.makovskyi.notificator.dsl.NotificationMarker
import ua.makovskyi.notificator.dsl.PendingIntentMarker
import ua.makovskyi.notificator.dsl.TaskStackMarker
import ua.makovskyi.notificator.utils.buildMessage
import ua.makovskyi.notificator.utils.fromFirst
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

@TaskStackMarker
@PendingIntentMarker
class PendingIntentBuilder {

    private var requestCode: Int = 100
    private var pendingFlags: Int = PendingIntent.FLAG_UPDATE_CURRENT
    private var targetIntent: From? = null
    private var packageContext: Context? = null
    private var taskStackElements: List<TaskStackElement> = listOf()

    fun requestCode(init: () -> Int?) {
        requestCode = init() ?: return
    }

    fun pendingFlags(init: (MutableList<Int>) -> Unit) {
        pendingFlags = mutableListOf<Int>()
            .apply(init)
            .reduce { acc, flag ->
                acc or flag
            }
    }

    fun targetIntent(init: () -> From?) {
        targetIntent = init()
    }

    fun packageContext(init: () -> Context?) {
        packageContext = init()
    }

    fun taskStackElements(init: (MutableList<TaskStackElement>) -> Unit) {
        taskStackElements = mutableListOf<TaskStackElement>().apply(init)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun build() : PendingIntent {
        require(taskStackElements.isNotEmpty()) {
            buildMessage(
                PendingIntentBuilder::class,
                "Can not create pending intent from empty tasks list")
        }
        val context = requireNotNull(packageContext) {
            buildMessage(
                PendingIntentBuilder::class,
                "To create pending intent, please pass your package context")
        }
        return when(targetIntent) {
            From.SERVICE -> {
                val intent = taskStackElements.fromFirst { e -> e?.intent }
                if (intent != null) {
                    PendingIntent.getService(context, requestCode, intent, pendingFlags)
                } else {
                    throw IllegalArgumentException(
                        buildMessage(
                            PendingIntentBuilder::class,
                            "Can not create pending intent from empty intent"))
                }
            }
            From.ACTIVITY -> {
                if (taskStackElements.isSingle()) {
                    taskStackElements.fromFirst { e -> e?.intent }.let { intent ->
                        PendingIntent.getActivity(context, requestCode, intent, pendingFlags)
                    }

                } else {
                    requireNotNull(TaskStackBuilder.create(context).run {
                        for (element in taskStackElements) {
                            element.intent.safe { intent ->
                                when(element.howPut) {
                                    HowPut.ONLY_NEXT_INTENT -> addNextIntent(intent)
                                    HowPut.ONLY_EXTRACT_PARENT -> addParentStack(intent.component)
                                    HowPut.NEXT_INTENT_WITH_PARENT -> addNextIntentWithParentStack(intent)
                                }
                            }
                        }
                        getPendingIntent(requestCode, pendingFlags)
                    })
                }
            }
            From.BROADCAST -> {
                val intent = taskStackElements.fromFirst { e -> e?.intent }
                if (intent != null) {
                    PendingIntent.getBroadcast(context, requestCode, intent, pendingFlags)
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

    internal fun build(init: PendingIntentBuilder.() -> Unit): PendingIntent {
        init()
        return build()
    }
}

class Intention private constructor(
    internal val autoCancel: Boolean,
    internal val deleteIntent: PendingIntent?,
    internal val contentIntent: PendingIntent?
) {

    @NotificationMarker
    @PendingIntentMarker
    class Builder(
        private var autoCancel: Boolean = true,
        private var deleteIntent: PendingIntent? = null,
        private var contentIntent: PendingIntent? = null
    ) {

        fun autoCancel(init: () -> Boolean?) {
            autoCancel = init() ?: return
        }

        fun deleteIntent(builder: PendingIntentBuilder) {
            deleteIntent = builder.build()
        }

        fun deleteIntent(init: PendingIntentBuilder.() -> Unit) {
            deleteIntent = PendingIntentBuilder().build(init)
        }

        fun contentIntent(builder: PendingIntentBuilder) {
            contentIntent = builder.build()
        }

        fun contentIntent(init: PendingIntentBuilder.() -> Unit) {
            contentIntent = PendingIntentBuilder().build(init)
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun build(): Intention = Intention(autoCancel, deleteIntent, contentIntent)

        internal fun build(init: Builder.() -> Unit): Intention {
            init()
            return build()
        }
    }
}

fun notificationIntention(init: Intention.Builder.() -> Unit): Intention = Intention.Builder().build(init)