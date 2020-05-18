package ua.makovskyi.notificator.utils

import kotlin.reflect.KClass

import android.net.Uri
import android.os.Build
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager

import androidx.annotation.DrawableRes

/**
 * @author Denis Makovskyi
 */

fun bitmapFromResources(context: Context, @DrawableRes id: Int): Bitmap? {
    return BitmapFactory.decodeResource(context.resources, id)
}

fun defaultNotificationSound(): Uri {
    return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
}

internal fun isApiLevel(level: Int): Boolean {
    return Build.VERSION.SDK_INT >= level
}

internal fun buildMessage(cls: KClass<*>, message: String): String {
    return "${cls.java.name}: $message"
}