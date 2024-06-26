@file:Suppress("UNCHECKED_CAST")

package cc.mewcraft.commons.reflection

import kotlin.jvm.internal.CallableReference
import kotlin.jvm.internal.PropertyReference0Impl
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.KProperty2
import kotlin.reflect.full.memberExtensionProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun KProperty0<*>.hasRuntimeDelegate(): Boolean {
    return getRuntimeDelegate() != null
}

fun KProperty0<*>.getRuntimeDelegate(): Any? {
    require(this is CallableReference)
    
    val receiver = boundReceiver
    if (receiver == CallableReference.NO_RECEIVER) {
        isAccessible = true
        return getDelegate()
    }
    
    val property = receiver::class.memberProperties.first { it.name == name } as KProperty1<Any, *>
    property.isAccessible = true
    return property.getDelegate(receiver)
}

fun <T> KProperty1<T, *>.hasRuntimeDelegate(receiver: T & Any): Boolean {
    return getRuntimeDelegate(receiver) != null
}

fun <T> KProperty1<T, *>.getRuntimeDelegate(receiver: T & Any): Any? {
    val property = receiver::class.memberProperties.first { it.name == name } as KProperty1<T, *>
    property.isAccessible = true
    return property.getDelegate(receiver)
}

fun <T, E> KProperty2<T, E, *>.hasRuntimeDelegate(receiver: T & Any): Boolean {
    return getRuntimeDelegate(receiver) != null
}

fun <T, E> KProperty2<T, E, *>.getRuntimeDelegate(receiver: T & Any): Any? {
    val property = receiver::class.memberExtensionProperties.first { it.name == name } as KProperty2<T, E, *>
    property.isAccessible = true
    return property.getDelegate(receiver, null as E)
}

fun KProperty0<*>.isLazyInitialized(): Boolean {
    isAccessible = true
    val lazy = getDelegate() as? Lazy<*>
    return lazy?.isInitialized() ?: false
}

fun KProperty0<*>.isRuntimeLazyInitialized(): Boolean {
    val lazy = getRuntimeDelegate() as? Lazy<*>
    return lazy?.isInitialized() ?: false
}

fun <T> KProperty1<T, *>.isLazyInitialized(receiver: T): Boolean {
    isAccessible = true
    val lazy = getDelegate(receiver) as? Lazy<*>
    return lazy?.isInitialized() ?: false
}

fun <T> KProperty1<T, *>.isRuntimeLazyInitialized(receiver: T & Any): Boolean {
    val lazy = getRuntimeDelegate(receiver) as? Lazy<*>
    return lazy?.isInitialized() ?: false
}

fun <T, E> KProperty2<T, E, *>.isLazyInitialized(receiver1: T, receiver2: E): Boolean {
    isAccessible = true
    val lazy = getDelegate(receiver1, receiver2) as? Lazy<*>
    return lazy?.isInitialized() ?: false
}

fun <T, E> KProperty2<T, E, *>.isRuntimeLazyInitialized(receiver: T & Any): Boolean {
    val lazy = getRuntimeDelegate(receiver) as? Lazy<*>
    return lazy?.isInitialized() ?: false
}