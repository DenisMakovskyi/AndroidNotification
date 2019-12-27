package ua.makovskyi.notificator.utils

import kotlin.reflect.KClass

/**
 * @author Denis Makovskyi
 */

internal fun buildMessage(cls: KClass<*>, message: String): String =
    "${cls.java.name}: $message"