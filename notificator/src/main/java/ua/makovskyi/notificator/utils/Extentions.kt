package ua.makovskyi.notificator.utils

import kotlin.contracts.contract
import kotlin.contracts.InvocationKind
import kotlin.contracts.ExperimentalContracts

import android.content.Context
import android.os.Build

import androidx.annotation.RestrictTo

/**
 * @author Denis Makovskyi
 */

@RestrictTo(RestrictTo.Scope.LIBRARY)
@UseExperimental(ExperimentalContracts::class)
internal inline fun <T> T.only(block: (T) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block(this)
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
@UseExperimental(ExperimentalContracts::class)
internal inline fun <T> T?.safe(block: (T) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (this != null) block(this)
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal fun Collection<*>.isSingle(): Boolean {
    return this.size == 1
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal fun <T, R> List<T>.fromFirst(block: (T?) -> R?): R? {
    if (isEmpty()) throw NoSuchElementException("List is empty.")
    return block(this[0])
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal inline fun <reified T> Context.findSystemService(): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getSystemService(T::class.java)
    } else {
        val name = getSystemServiceName(T::class.java)
        if (name != null) getSystemService(name) as T? else null
    }
}