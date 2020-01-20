package ua.makovskyi.notificator.firebase

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

/**
 * Created by Denis Makovskyi
 */

internal fun randomId(): Int {
    return (Math.random() * (Short.MAX_VALUE - Short.MIN_VALUE)).toInt()
}

internal fun iconFromMetaData(context: Context): Int {
    val info = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)

    val iconRes = info.metaData.getInt("com.google.firebase.messaging.default_notification_icon")
    val appIcon = info.icon

    return if (iconRes != 0) iconRes else appIcon
}

@ColorRes
internal fun colorFromMetaData(context: Context): Int {
    val info = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    return info.metaData.getInt("com.google.firebase.messaging.default_notification_color")
}

@DrawableRes
internal fun iconFromResources(context: Context, name: String): Int {
    return context.resources.getIdentifier(name, "int", context.packageName)
}

internal fun stringFromResources(context: Context, name: String): String {
    val resId = context.resources.getIdentifier(name, "string", context.packageName)
    return context.getString(resId)
}

internal fun drawableToBitmap(drawable: Drawable?): Bitmap? {
    if (drawable == null) return null

    if (drawable is BitmapDrawable) {
        if (drawable.bitmap != null) return drawable.bitmap
    }

    if (drawable.intrinsicWidth <= 0 && drawable.intrinsicHeight <= 0) return null

    return Bitmap.createBitmap(
        drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    ).also {
        val canvas = Canvas(it)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
    }
}