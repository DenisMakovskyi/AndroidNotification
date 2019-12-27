package ua.makovskyi.notificator.utils

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable

/**
 * @author Denis Makovskyi
 */

internal fun <T> T?.safe(block: (T) -> Unit) {
    if (this != null) block(this)
}

internal fun <T> T?.safeOr(block: (T) -> Unit, otherwise: () -> Unit) {
    if (this != null) block(this) else otherwise()
}

internal fun <T> T.only(block: (T) -> Unit) {
    block(this)
}

internal fun Collection<*>.isSingle(): Boolean {
    return this.size == 1
}

internal fun <V> Map<String, V?>.toBundle(): Bundle {
    val bundle = Bundle(size)
    for (entry in this) {
        when(val value = entry.value) {
            null -> bundle.putString(entry.key, null)

            // Scalars
            is Boolean -> bundle.putBoolean(entry.key, value)
            is Byte -> bundle.putByte(entry.key, value)
            is Char -> bundle.putChar(entry.key, value)
            is Double -> bundle.putDouble(entry.key, value)
            is Float -> bundle.putFloat(entry.key, value)
            is Int -> bundle.putInt(entry.key, value)
            is Long -> bundle.putLong(entry.key, value)
            is Short -> bundle.putShort(entry.key, value)

            // References
            is Bundle -> bundle.putBundle(entry.key, value)
            is CharSequence -> bundle.putCharSequence(entry.key, value)
            is Parcelable -> bundle.putParcelable(entry.key, value)

            // Scalar arrays
            is BooleanArray -> bundle.putBooleanArray(entry.key, value)
            is ByteArray -> bundle.putByteArray(entry.key, value)
            is CharArray -> bundle.putCharArray(entry.key, value)
            is DoubleArray -> bundle.putDoubleArray(entry.key, value)
            is FloatArray -> bundle.putFloatArray(entry.key, value)
            is IntArray -> bundle.putIntArray(entry.key, value)
            is LongArray -> bundle.putLongArray(entry.key, value)
            is ShortArray -> bundle.putShortArray(entry.key, value)
        }
    }
    return bundle
}

internal inline fun <reified T> Context.findSystemService(): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getSystemService(T::class.java)
    } else {
        val name = getSystemServiceName(T::class.java)
        if (name != null) getSystemService(name) as T? else null
    }
}