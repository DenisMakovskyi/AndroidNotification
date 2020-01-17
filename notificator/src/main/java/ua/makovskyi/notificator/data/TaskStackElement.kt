package ua.makovskyi.notificator.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

import androidx.annotation.RestrictTo

import ua.makovskyi.notificator.dsl.IntentMarker
import ua.makovskyi.notificator.dsl.TaskStackMarker
import ua.makovskyi.notificator.utils.buildMessage
import ua.makovskyi.notificator.utils.safe

/**
 * @author Denis Makovskyi
 */

enum class ConstructFrom {
    ACTION,
    COMPONENT_NAME
}

@IntentMarker
class IntentBuilder {

    private var from: ConstructFrom = ConstructFrom.COMPONENT_NAME
    private var context: Context? = null
    private var targetClass: Class<*>? = null
    private var intentData: Uri? = null
    private var intentAction: String? = null
    private var intentExtras: Bundle? = null
    private var intentBehaviour: List<Int>? = null
    private var intentCategories: List<String>? = null

    fun from(init: () -> ConstructFrom) {
        from = init()
    }

    fun context(init: () -> Context?) {
        context = init()
    }

    fun targetClass(init: () -> Class<*>?) {
        targetClass = init()
    }

    fun intentData(init: () -> Uri?) {
        intentData = init()
    }

    fun intentAction(init: () -> String?) {
        intentAction = init()
    }

    fun intentExtras(init: () -> Bundle?) {
        intentExtras = init()
    }

    fun intentBehaviour(vararg flags: Int) {
        intentBehaviour = flags.toList()
    }

    fun intentCategories(vararg categories: String) {
        intentCategories = categories.toList()
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun build(): Intent {
        return when(from) {
            ConstructFrom.ACTION -> {
                requireNotNull(intentAction) {
                    buildMessage(
                        IntentBuilder::class,
                        "Can not create intent from empty action")
                }
                Intent(intentAction)
            }
            ConstructFrom.COMPONENT_NAME -> {
                requireNotNull(context) {
                    buildMessage(
                        IntentBuilder::class,
                        "Can not create intent from empty package context")
                }
                requireNotNull(targetClass) {
                    buildMessage(
                        IntentBuilder::class,
                        "Can not create intent from empty target class")
                }
                Intent(context, targetClass).also { intent ->
                    this@IntentBuilder.intentAction.safe { action ->
                        intent.action = action
                    }
                }
            }
        }.also { intent ->
            this@IntentBuilder.intentData.safe { data ->
                intent.data = data
            }
            this@IntentBuilder.intentExtras.safe { bundle ->
                intent.putExtras(bundle)
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
        }
    }

    internal fun build(init: IntentBuilder.() -> Unit): Intent {
        init()
        return build()
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

        fun intent(builder: IntentBuilder) {
            intent = builder.build()
        }

        fun intent(init: IntentBuilder.() -> Unit) {
            intent = IntentBuilder().build(init)
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun build(): TaskStackElement = TaskStackElement(howPut, intent)

        internal fun build(init: Builder.() -> Unit): TaskStackElement {
            init()
            return build()
        }
    }
}

fun taskStackElement(init: TaskStackElement.Builder.() -> Unit): TaskStackElement = TaskStackElement.Builder().build(init)