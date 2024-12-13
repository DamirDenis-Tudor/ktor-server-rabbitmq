package com.mesh.kabbitMq.util

import kotlin.reflect.KProperty

class StateDelegator<T : Any> {

    companion object {
        sealed class InitializationState<out T> {
            data class Initialized<T>(val value: T) : InitializationState<T>()
            data object Uninitialized : InitializationState<Nothing>()
        }

        private val stateMap = mutableMapOf<String, InitializationState<Any>>()

        private fun <T : Any> setInitialized(property: KProperty<*>, value: T) {
            stateMap[property.name] = InitializationState.Initialized(value)
        }

        fun initialized(property: KProperty<*>): Boolean {
            return stateMap[property.name] is InitializationState.Initialized
        }

        fun <T : Any> delegate(property: KProperty<*>): StateDelegator<T> {
            return StateDelegator<T>().apply {
                stateMap.putIfAbsent(property.name, InitializationState.Uninitialized)
            }
        }
    }

    private var state: InitializationState<T> = InitializationState.Uninitialized

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return when (val currentState = state) {
            is InitializationState.Initialized -> currentState.value
            else -> throw UninitializedPropertyAccessException("Property ${property.name} must be initialized before accessing.")
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        state = InitializationState.Initialized(value)
        setInitialized(property, value)
    }
}