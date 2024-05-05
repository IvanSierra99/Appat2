package com.example.appat.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlinx.coroutines.CancellationException

/**
 * This class is used to provide a (monad) result for an operation, which may be successful or fail.
 *
 * This class provides an API to facilitate working with results. In summary:
 * - Constructing: [appRunCatching], [asSuccess], [asFailure].
 * - Mapping: [map], [mapError], [mapBoth], [or].
 * - FlatMapping (monadic): [andThen].
 * - Handling the value: [onSuccess], [onFailure], [recover], [getOrNull].
 * - The raise DSL from arrow can help improve readability in some cases
 *
 * Based on https://github.com/michaelbull/kotlin-result with changes and additions.
 */
sealed class AppResult<out V, out E> {
    internal abstract val isSuccess: Boolean
    internal abstract val isFailure: Boolean

    @OptIn(ExperimentalContracts::class)
    fun isFailure(): Boolean {
        contract {
            returns(true) implies (this@AppResult is Failure<E>)
            returns(false) implies (this@AppResult is Success<V>)
        }
        return this@AppResult is Failure<E>
    }

    @OptIn(ExperimentalContracts::class)
    fun isSuccess(): Boolean {
        contract {
            returns(true) implies (this@AppResult is Success<V>)
            returns(false) implies (this@AppResult is Failure<E>)
        }
        return this@AppResult is Success<V>
    }

    companion object
}

data class Success<out V>(val value: V) : AppResult<V, Nothing>() {
    override val isSuccess: Boolean = true
    override val isFailure: Boolean = false
}

data class Failure<out E>(val error: E) : AppResult<Nothing, E>() {
    override val isSuccess: Boolean = false
    override val isFailure: Boolean = true
}

/**
 * Build a [Success] from [this]
 * @return [Success] with the receiver as value
 */
fun <T> T.asSuccess() = Success(this)

/**
 * Build a [Failure] from [this]
 * @return [Failure] with the receiver as value
 */
fun <T> T.asFailure() = Failure(this)

/**
 * Calls [block] in a try that returns a [AppResult]. The [CancellationException] is not captured
 * to maintain the structured concurrency.
 *
 * @return
 * A [Success] with the result of [block], if no exception is thrown.
 *
 * A [Failure] with the Throwable, if an exception is thrown.
 */
inline fun <V> appRunCatching(block: () -> V): AppResult<V, Throwable> {
    return try {
        block().asSuccess()
    } catch (e: Throwable) {
        e.asFailure()
    }
}

/**
 * Calls [block] with [this] as receiver, in a try that returns a [AppResult].
 * The [CancellationException] is not captured to maintain the structured concurrency.
 *
 * @return
 * A [Success] with the result of [block], if no exception is thrown.
 *
 * A [Failure] with the Throwable, if an exception is thrown.
 */
inline infix fun <T, V> T.appRunCatching(block: T.() -> V): AppResult<V, Throwable> {
    return try {
        block().asSuccess()
    } catch (e: Throwable) {
        e.asFailure()
    }
}

/**
 * Transforms the success branch, to modify it's value, and potentially, its type.
 *
 * @return
 * If [this] is [Success], a [Success] of [transform] of the value
 *
 * If [this] is [Failure] it returns [this].
 */
inline infix fun <V, E, U> AppResult<V, E>.map(transform: (V) -> U): AppResult<U, E> {
    return when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }
}

/**
 * Transforms the failure branch, to modify it's value, and potentially, its type.
 *
 * @return
 * If [this] is [Success], [this].
 *
 * If [this] is [Failure], a [Failure] of [transform] of the value.
 */
inline infix fun <V, E, F> AppResult<V, E>.mapError(transform: (E) -> F): AppResult<V, F> {
    return when (this) {
        is Success -> this
        is Failure -> Failure(transform(error))
    }
}

/**
 * Transforms both branches, applying each transformation to the proper side.
 *
 * @return
 * If [this] is [Success], [Success] with [success] applied to its value.
 *
 * If [this] is [Failure], [Failure] with [failure] applied to its value.
 */
inline fun <V, E, U> AppResult<V, E>.mapBoth(success: (V) -> U, failure: (E) -> U): U {
    return when (this) {
        is Success -> success(value)
        is Failure -> failure(error)
    }
}

/**
 * FlatMap for the success branch.
 * Useful for executing new operations that may fail, only when we are on a success.
 *
 * @return
 * If [this] is [Success], result of [transform].
 *
 * If [this] is [Failure], [this].
 */
inline infix fun <V, E, U> AppResult<V, E>.andThen(
    transform: (V) -> AppResult<U, E>,
): AppResult<U, E> = when (this) {
    is Success -> transform(value)
    is Failure -> this
}

/**
 * Invokes an [action] if this [MyResult] is [Success].
 */
inline infix fun <V, E> AppResult<V, E>.onSuccess(action: (V) -> Unit): AppResult<V, E> {
    if (this is Success) {
        action(value)
    }
    return this
}

/**
 * Invokes an [action] if this [MyResult] is [Failure].
 */
inline infix fun <V, E> AppResult<V, E>.onFailure(action: (E) -> Unit): AppResult<V, E> {
    if (this is Failure) {
        action(error)
    }
    return this
}
