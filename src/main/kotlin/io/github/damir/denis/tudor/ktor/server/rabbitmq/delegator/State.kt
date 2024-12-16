package io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator

/**
 * Sealed class representing the state of a property, which can either be initialized with a value
 * or uninitialized.
 *
 * @param T the type of the value contained in the `Initialized` state.
 *
 * @author Damir Denis-Tudor
 * @version 0.1.0
 */
internal sealed class State<out T> {
    /**
     * Represents an initialized state with a value.
     *
     * @param T the type of the value being initialized.
     * @param value the value associated with this state.
     */
    data class Initialized<T>(val value: T) : State<T>()

    /**
     * Represents an uninitialized state, used when a property has not been set yet.
     */
    data object Uninitialized : State<Nothing>()
}