package com.mesh.kabbitMq.util

import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

sealed class State<out T> {
    data class Initialized<T>(val value: T) : State<T>()
    data object Uninitialized : State<Nothing>()
}

class StateDelegator<T : Any>(
    private var state: State<T> = State.Uninitialized
) {
    companion object {
        private val stateMap = mutableMapOf<Pair<String, String>, State<Any>>()
        private lateinit var ref: Any

        fun initialized(vararg properties: KProperty<*>, thisRef: Any = ref): Boolean {
            return properties.all {
                stateMap.getOrPut(thisRef.javaClass.simpleName to it.name) { State.Uninitialized } !is State.Uninitialized
            }
        }

        fun stateTrace(thisRef: Any = ref): String {
            return thisRef::class.memberProperties.joinToString("\n") {
                "${thisRef.javaClass.simpleName}: <${it.name}> -> ${(stateMap[thisRef.javaClass.simpleName to it.name] is State.Initialized)}"
            }
        }

        fun withThisRef(ref: Any, block: Companion.() -> Unit) {
            this.ref = ref
            this.apply(block)
            this.ref = Any()
        }
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return when (val currentState = state) {
            is State.Initialized -> currentState.value
            else -> throw UninitializedPropertyAccessException(
                "Property <${property.javaClass}>: <${property.name}> must be initialized before accessing."
            )
        }
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        state = State.Initialized(value)
        stateMap[thisRef.javaClass.simpleName to property.name] = State.Initialized(value)
    }
}
