package com.mesh.kabbitMq.util

import kotlin.reflect.KProperty

sealed class State<out T> {
    data class Initialized<T>(val value: T) : State<T>()
    data object Uninitialized : State<Nothing>()
}

class StateDelegator<T : Any>(
    private var state: State<T> = State.Uninitialized
) {

    companion object {
        private val stateMap = mutableMapOf<String, State<Any>>()

        fun initialized(property: KProperty<*>): Boolean {
            return stateMap[property.name] is State.Initialized
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return when (val currentState = state) {
            is State.Initialized -> currentState.value
            else -> throw UninitializedPropertyAccessException("Property ${property.name} must be initialized before accessing.")
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        state = State.Initialized(value)
        stateMap[property.name] = State.Initialized(value)
    }
}