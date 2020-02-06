package ua.makovskyi.notificator.data

import androidx.annotation.RestrictTo

import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

data class Identifier constructor(
    val id: Int,
    val sortKey: String?,
    val groupKey: String?
) {

    @NotificationMarker
    class Builder(
        private var id: Int = 0,
        private var sortKey: String? = null,
        private var groupKey: String? = null
    ) {

        constructor(identifier: Identifier) : this(
            identifier.id,
            identifier.sortKey,
            identifier.groupKey)

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