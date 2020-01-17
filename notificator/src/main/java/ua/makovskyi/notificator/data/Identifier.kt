package ua.makovskyi.notificator.data

import androidx.annotation.RestrictTo

import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

class Identifier private constructor(
    internal val id: Int,
    internal val sortKey: String?,
    internal val groupKey: String?
) {

    @NotificationMarker
    class Builder(
        private var id: Int = 0,
        private var sortKey: String? = null,
        private var groupKey: String? = null
    ) {

        fun id(init: () -> Int) {
            id = init()
        }

        fun sortKey(init: () -> String?) {
            sortKey = init()
        }

        fun groupKey(init: () -> String?) {
            groupKey = init()
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun build(): Identifier = Identifier(id, sortKey, groupKey)

        internal fun build(init: Builder.() -> Unit): Identifier {
            init()
            return build()
        }
    }
}

fun notificationIdentifier(init: Identifier.Builder.() -> Unit): Identifier = Identifier.Builder().build(init)