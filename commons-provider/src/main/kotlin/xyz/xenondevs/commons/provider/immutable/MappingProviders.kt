package xyz.xenondevs.commons.provider.immutable

import xyz.xenondevs.commons.provider.AbstractProvider
import xyz.xenondevs.commons.provider.Provider

/**
 * Creates and returns a new [Provider] that maps the value of [this][Provider]
 * using the [transform] function.
 */
fun <T, R> Provider<T>.map(transform: (T) -> R): Provider<R> {
    val provider = MappingProvider(this, transform)
    addChild(provider)
    return provider
}

/**
 * Creates and returns a new [Provider] that maps non-null values of [this][Provider]
 * using the [transform] function.
 * Null values will be passed through without transformation.
 */
inline fun <T : Any, R> Provider<T?>.mapNonNull(crossinline transform: (T) -> R): Provider<R?> =
    map { it?.let(transform) }

/**
 * Creates and returns a new [Provider] that maps each element of the [Collection] obtained from [this][Provider]
 * using the [transform] function.
 */
inline fun <T, R> Provider<Collection<T>>.mapEach(crossinline transform: (T) -> R): Provider<List<R>> =
    mapEachTo({ size -> ArrayList(size) }, transform)

/**
 * Creates and returns a new [Provider] that maps each element of the [Collection] obtained from [this][Provider]
 * using the [transform] function and adds the results to a collection created by [makeCollection].
 */
inline fun <T, R, C : MutableCollection<in R>> Provider<Collection<T>>.mapEachTo(
    crossinline makeCollection: (size: Int) -> C,
    crossinline transform: (T) -> R
): Provider<C> = map { it.mapTo(makeCollection(it.size), transform) }

/**
 * Creates and returns a new [Provider] that maps each element of the [Collection] obtained from [this][Provider]
 * using the [transform] function and filters out all null results.
 */
inline fun <T, R : Any> Provider<Collection<T>>.mapEachNotNull(crossinline transform: (T) -> R?): Provider<List<R>> = 
    mapEachNotNullTo(::ArrayList, transform)

/**
 * Creates and returns a new [Provider] that maps each element of the [Collection] obtained from [this][Provider]
 * using the [transform] function and filters out all null results.
 * The results are added to a collection created by [makeCollection].
 */
inline fun <T, R : Any, C : MutableCollection<in R>> Provider<Collection<T>>.mapEachNotNullTo(
    crossinline makeCollection: (size: Int) -> C,
    crossinline transform: (T) -> R?
): Provider<C> = map { it.mapNotNullTo(makeCollection(it.size), transform) }

/**
 * Creates and returns a new [Provider] that flat-maps the elements of the [Collection] obtained from [this][Provider]
 * into a list using the [transform] function.
 */
inline fun <T, R> Provider<Collection<T>>.flatMap(crossinline transform: (T) -> Iterable<R>): Provider<List<R>> =
    flatMapTo({ size -> ArrayList(size) }, transform)

/**
 * Creates and returns a new [Provider] that flat-maps the elements of the [Collection] obtained from [this][Provider]
 * into a collection created by [makeCollection] using the [transform] function.
 */
inline fun <T, R, C : MutableCollection<in R>> Provider<Collection<T>>.flatMapTo(
    crossinline makeCollection: (size: Int) -> C,
    crossinline transform: (T) -> Iterable<R>
): Provider<C> = map { it.flatMapTo(makeCollection(it.size), transform) }

/**
 * Creates and returns a new [Provider] that flattens the [List] of [Lists][List] obtained from [this][Provider].
 */
fun <T> Provider<Iterable<Iterable<T>>>.flatten(): Provider<List<T>> =
    map { it.flatten() }

/**
 * Creates and returns a new [Provider] that merges all [Maps][Map] obtained from [this][Provider] into a single [Map].
 */
fun <K, V> Provider<List<Map<K, V>>>.merged(): Provider<Map<K, V>> =
    mergedTo(::HashMap)

/**
 * Creates and returns a new [Provider] that merges all [Maps][Map] obtained from [this][Provider] into a single [Map],
 * which is created by the [makeMap] function.
 */
fun <K, V, M: MutableMap<in K, in V>> Provider<List<Map<K, V>>>.mergedTo(makeMap: (size: Int) -> M): Provider<M> =
    map { maps ->
        val size = maps.sumOf { it.size }
        val map = makeMap(size)
        maps.forEach(map::putAll)
        map
    }

/**
 * Creates and returns a new [Provider] that throws an [IllegalArgumentException]
 * with a message generated by [message] if [condition] fails.
 */
inline fun <T> Provider<T>.require(
    crossinline condition: (T) -> Boolean,
    crossinline message: (T) -> String
): Provider<T> = map { require(condition(it)) { message(it) }; it }

/**
 * Creates and returns a new [Provider] that throws an [IllegalArgumentException]
 * with [message] if the value is `null`.
 */
fun <T : Any> Provider<T?>.requireNotNull(message: String = "Required value was null."): Provider<T> =
    requireNotNull { message }

/**
 * Creates and returns a new [Provider] that throws an [IllegalArgumentException]
 * with a message generated by [message] if the value is `null`.
 */
inline fun <T : Any> Provider<T?>.requireNotNull(crossinline message: () -> String): Provider<T> =
    map { requireNotNull(it, message); it }

private class MappingProvider<T, R>(
    private val provider: Provider<T>,
    private val transform: (T) -> R
) : AbstractProvider<R>() {
    override fun loadValue(): R {
        return transform(provider.get())
    }
}