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
    private var intentBehaviour: MutableList<Int> = mutableListOf()
    private var intentCategories: MutableList<String> = mutableListOf()

    operator fun String.unaryPlus() {
        intentCategories.add(this)
    }

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

    fun intentBehaviour(init: () -> Int) {
        intentBehaviour.add(init())
    }

    @Deprecated(
        "Use DSL-style function instead",
        ReplaceWith(
            "intentBehaviour(init: () -> Int)",
            "ua.makovskyi.notificator.data"
        ),
        DeprecationLevel.WARNING
    )
    fun intentBehaviour(vararg flags: Int) {
        intentBehaviour = flags.toMutableList()
    }

    fun intentCategory(init: () -> String) {
        +init()
    }

    @Deprecated(
        "Use DSL-style function instead",
        ReplaceWith(
            "intentCategory(init: () -> String)",
            "ua.makovskyi.notificator.data"
        ),
        DeprecationLevel.WARNING
    )
    fun intentCategories(vararg categories: String) {
        intentCategories = categories.toMutableList()
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
            intentData.safe { data ->
                intent.data = data
            }
            intentExtras.safe { bundle ->
                intent.putExtras(bundle)
            }
            for (flag in intentBehaviour) {
                intent.addFlags(flag)
            }
            for (category in intentCategories) {
                intent.addCategory(category)
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
    val howPut: HowPut?,
    val intent: Intent?
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