package ua.makovskyi.notificator.utils

import kotlin.reflect.KClass

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri

import androidx.annotation.DrawableRes
import androidx.annotation.RestrictTo

/**
 * @author Denis Makovskyi
 */


fun bitmapFromResources(context: Context, @DrawableRes id: Int): Bitmap? {
    return BitmapFactory.decodeResource(context.resources, id)
}

fun defaultNotificationSound(): Uri {
    return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal fun buildMessage(cls: KClass<*>, message: String): String {
    return "${cls.java.name}: $message"
}