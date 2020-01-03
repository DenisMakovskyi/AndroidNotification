package ua.makovskyi.notificator.data

import android.content.Context
import android.content.Intent

import ua.makovskyi.notificator.dsl.IntentMarker
import ua.makovskyi.notificator.dsl.TaskStackMarker
import ua.makovskyi.notificator.utils.buildMessage
import ua.makovskyi.notificator.utils.safe
import ua.makovskyi.notificator.utils.toBundle

/**
 * @author Denis Makovskyi
 */

@IntentMarker
class IntentBuilder(
    private var context: Context? = null,
    private var targetClass: Class<*>? = null,
    private var intentAction: String? = null,
    private var intentBehaviour: List<Int>? = null,
    private var intentCategories: List<String>? = null,
    private var intentBundleExtras: Map<String, Any?>? = null
) {

    fun context(init: () -> Context) {
        context = init()
    }

    fun targetClass(init: () -> Class<*>) {
        targetClass = init()
    }

    fun intentAction(init: () -> String) {
        intentAction = init()
    }

    fun intentBehaviour(init: (MutableList<Int>) -> Unit) {
        intentBehaviour = mutableListOf<Int>().apply(init)
    }

    fun intentCategories(init: (MutableList<String>) -> Unit) {
        intentCategories = mutableListOf<String>().apply(init)
    }

    fun intentBundleExtras(init: (Map<String, Any?>) -> Unit) {
        intentBundleExtras = hashMapOf<String, Any?>().apply(init)
    }

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
        val cls = requireNotNull(targetClass) {
            buildMessage(
                IntentBuilder::class,
                "Can not create intent from empty target class")
        }
        return Intent(ctx, cls).also { intent ->
            this@IntentBuilder.intentAction.safe { action ->
                intent.action = action
            }
            this@IntentBuilder.intentBehaviour.safe { flags ->
                for (flag in flags) {
                    intent.addFlags(flag)
                }
            }
            this@IntentBuilder.intentCategories.safe { categories ->
                for (category in categories) {
                    intent.addCategory(category)
                }
            }
            this@IntentBuilder.intentBundleExtras.safe { arguments ->
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

class TaskStackElement private constructor(
    internal val howPut: HowPut?,
    internal val intent: Intent?
) {

    @IntentMarker
    @TaskStackMarker
    class Builder(
        private var howPut: HowPut = HowPut.ONLY_NEXT_INTENT,
        private var intent: Intent? = null
    ) {

        fun howPut(init: () -> HowPut) {
            howPut = init()
        }

        fun intent(init: IntentBuilder.() -> Unit) {
            intent = IntentBuilder().build(init)
        }

        internal fun build(init: Builder.() -> Unit): TaskStackElement {
            init()
            return build()
        }

        internal fun build(): TaskStackElement = TaskStackElement(howPut, intent)
    }
}

fun taskStackElement(init: TaskStackElement.Builder.() -> Unit): TaskStackElement = TaskStackElement.Builder().build(init)