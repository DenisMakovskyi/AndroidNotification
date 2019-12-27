package ua.makovskyi.notificator.data

import ua.makovskyi.notificator.dsl.NotificationMarker

/**
 * @author Denis Makovskyi
 */

class Identifier private constructor(
    val notificationId: Int,
    val groupKey: String?,
    val sortKey: String?
) {

    @NotificationMarker
    class Builder(
        var notificationId: Int = 0,
        var groupKey: String? = null,
        var sortKey: String? = null
    ) {

        internal fun build(init: Builder.() -> Unit): Identifier {
            init()
            return build()
        }

        internal fun build(): Identifier = Identifier(notificationId, groupKey, sortKey)
    }
}

fun notificationIdentifier(init: Identifier.Builder.() -> Unit): Identifier = Identifier.Builder().build(init)