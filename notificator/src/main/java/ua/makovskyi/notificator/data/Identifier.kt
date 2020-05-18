package ua.makovskyi.notificator.data

import androidx.annotation.RestrictTo

import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

data class Identifier(
    val id: Int,
    val ongoing: Boolean,
    val sortKey: String?,
    val groupKey: String?,
    val category: String?
) {

    @NotificationMarker
    class Builder(
        private var id: Int = 0,
        private var ongoing: Boolean = false,
        private var sortKey: String? = null,
        private var groupKey: String? = null,
        private var category: String? = null
    ) {

        constructor(identifier: Identifier) : this(
            identifier.id,
            identifier.ongoing,
            identifier.sortKey,
            identifier.groupKey,
            identifier.category)

        fun id(init: () -> Int) {
            id = init()
        }

        fun ongoing(init: () -> Boolean) {
            ongoing = init()
        }

        fun sortKey(init: () -> String?) {
            sortKey = init()
        }

        fun groupKey(init: () -> String?) {
            groupKey = init()
        }

        fun category(init: () -> String?) {
            category = init()
        }

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun build(): Identifier = Identifier(id, ongoing, sortKey, groupKey, category)

        internal fun build(init: Builder.() -> Unit): Identifier {
            init()
            return build()
        }
    }
}

fun notificationIdentifier(init: Identifier.Builder.() -> Unit): Identifier = Identifier.Builder().build(init)