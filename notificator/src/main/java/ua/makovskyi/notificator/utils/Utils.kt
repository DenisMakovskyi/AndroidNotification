package ua.makovskyi.notificator.utils

import kotlin.reflect.KClass

import android.media.RingtoneManager
import android.net.Uri

import androidx.annotation.RestrictTo

/**
 * @author Denis Makovskyi
 */

fun defaultNotificationSound(): Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal fun buildMessage(cls: KClass<*>, message: String): String =
    "${cls.java.name}: $message"