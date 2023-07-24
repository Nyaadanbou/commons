@file:Suppress("UNCHECKED_CAST")

package xyz.xenondevs.commons.provider.mutable

import xyz.xenondevs.commons.provider.Provider

fun <T : Any, R> MutableProvider<T>.map(transform: (T) -> R, untransform: (R) -> T): MutableProvider<R> {
    return MutableMapEverythingProvider(this, transform, untransform).also(::addChild)
}

@Deprecated("Use mapNonNull instead", ReplaceWith("mapNonNull(transform, untransform)"))
@JvmName("map1")
fun <T, R> MutableProvider<T?>.map(transform: (T & Any) -> R, untransform: (R & Any) -> T & Any): MutableProvider<R?> = mapNonNull(transform, untransform)

fun <T, R> MutableProvider<T?>.mapNonNull(transform: (T & Any) -> R, untransform: (R & Any) -> T & Any): MutableProvider<R?> {
    return MutableMapNonNullProvider(this, transform, untransform).also(::addChild)
}

fun <T, R : T & Any> MutableProvider<T?>.orElse(value: R): MutableProvider<R> {
    return MutableFallbackValueProvider(this, value).also(::addChild)
}

fun <T, R : T & Any> MutableProvider<T?>.orElse(provider: Provider<R>): MutableProvider<R> {
    return MutableFallbackProviderProvider(this, provider)
}

private class MutableMapEverythingProvider<T, R>(
    private val provider: MutableProvider<T>,
    private val transform: (T) -> R,
    private val untransform: (R) -> T
) : MutableProvider<R>() {
    
    override fun loadValue(): R {
        return transform(provider.value)
    }
    
    override fun setValue(value: R) {
        this._value = value
        provider.setValue(untransform(value))
    }
    
}

private class MutableMapNonNullProvider<T, R>(
    private val provider: MutableProvider<T>,
    private val transform: (T & Any) -> R,
    private val untransform: (R & Any) -> T & Any
) : MutableProvider<R?>() {
    
    override fun loadValue(): R? {
        return provider.value?.let(transform)
    }
    
    override fun setValue(value: R?) {
        this._value = value
        provider.setValue(value?.let(untransform) as T)
    }
    
}

private class MutableFallbackValueProvider<T, R : T & Any>(
    private val provider: MutableProvider<T?>,
    private val fallback: R
) : MutableProvider<R>() {
    
    override fun loadValue(): R {
        return (provider.value ?: fallback) as R
    }
    
    override fun setValue(value: R) {
        this._value = value
        provider.setValue((if (value == fallback) null else value) as T)
    }
    
}

private class MutableFallbackProviderProvider<T, R : T & Any>(
    private val provider: MutableProvider<T?>,
    private val fallback: Provider<R>
) : MutableProvider<R>() {
    
    override fun loadValue(): R {
        return (provider.value ?: fallback.value) as R
    }
    
    override fun setValue(value: R) {
        this._value = value
        provider.setValue((if (value == fallback.value) null else value) as T)
    }
    
}
