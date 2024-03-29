package cc.mewcraft.commons.provider.immutable

import cc.mewcraft.commons.provider.Provider

@JvmName("providerLambda")
fun <T> provider(loadValue: () -> T): Provider<T> =
    LambdaProvider(loadValue)

@JvmName("providerStatic")
fun <T> provider(value: T): Provider<T> =
    ProviderWrapper(value)

internal class LambdaProvider<T>(private val loader: () -> T) : Provider<T>() {
    
    override fun loadValue(): T {
        return loader()
    }
    
}

internal class ProviderWrapper<T>(private val staticValue: T) : Provider<T>() {
    
    override fun loadValue(): T {
        return staticValue
    }
    
}

object NullProvider : Provider<Any?>() {
    
    override fun loadValue(): Any? {
        return null
    }
    
}