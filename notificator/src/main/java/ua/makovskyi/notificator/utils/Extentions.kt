package ua.makovskyi.notificator.utils

import kotlin.contracts.contract
import kotlin.contracts.InvocationKind
import kotlin.contracts.ExperimentalContracts

import android.os.Bundle
import android.os.Parcelable

import androidx.annotation.RestrictTo

/**
 * @author Denis Makovskyi
 */

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun String?.isTextMaxLengthExceeded(): Boolean {
    if (this == null) return false
    return this.length >= 30
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun <V> Map<String, V?>.toBundle(): Bundle {
    val bundle = Bundle(size)
    for (entry in this) {
        when (val value = entry.value) {
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

@OptIn(ExperimentalContracts::class)
internal inline fun <T> T.only(block: (T) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block(this)
}

@OptIn(ExperimentalContracts::class)
internal inline fun <T> T?.safe(block: (T) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (this != null) block(this)
}

internal fun Collection<*>.isSingle(): Boolean {
    return this.size == 1
}

internal fun <T, R> List<T>.fromFirst(block: (T?) -> R?): R? {
    if (isEmpty()) throw NoSuchElementException("List is empty.")
    return block(this[0])
}