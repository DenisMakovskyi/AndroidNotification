package ua.makovskyi.notificator.data

import android.content.Context
import android.content.Intent

import ua.makovskyi.notificator.dsl.IntentMarker
import ua.makovskyi.notificator.utils.buildMessage
import ua.makovskyi.notificator.utils.safe
import ua.makovskyi.notificator.utils.toBundle

/**
 * @author Denis Makovskyi
 */

@IntentMarker
class IntentBuilder(
    var context: Context? = null,
    var target: Class<*>? = null,
    var action: String? = null,
    var launchFlags: List<Int>? = null,
    var categories: List<String>? = null,
    var extraArguments: Map<String, Any?>? = null
) {

    internal fun build(init: IntentBuilder.() -> Unit): Intent {
        init()
        return build()
    }

    internal fun build(): Intent {
        val ctx = requireNotNull(context) {
            buildMessage(
                IntentBuilder::class,
                "Can not create intent from empty package context")
        }
        val cls = requireNotNull(target) {
            buildMessage(
                IntentBuilder::class,
                "Can not create intent from empty target")
        }
        return Intent(ctx, cls).also { intent ->
            this@IntentBuilder.action.safe { action ->
                intent.action = action
            }
            this@IntentBuilder.launchFlags.safe { flags ->
                for (flag in flags) {
                    intent.addFlags(flag)
                }
            }
            this@IntentBuilder.categories.safe { categories ->
                for (category in categories) {
                    intent.addCategory(category)
                }
            }
            this@IntentBuilder.extraArguments.safe { arguments ->
                intent.putExtras(arguments.toBundle())
            }
        }
    }
}

enum class HowPut {
    ONLY_NEXT_INTENT,
    ONLY_EXTRACT_PARENT,
    NEXT_INTENT_WITH_PARENT
}

class TaskElement private constructor(
    val howPut: HowPut?,
    val intent: Intent?
) {

    @IntentMarker
    class Builder(
        var howPut: HowPut = HowPut.ONLY_NEXT_INTENT,
        var intent: Intent? = null
    ) {

        fun intent(init: IntentBuilder.() -> Unit) {
            intent = IntentBuilder().build(init)
        }

        internal fun build(init: Builder.() -> Unit): TaskElement {
            init()
            return build()
        }

        internal fun build(): TaskElement = TaskElement(howPut, intent)
    }
}