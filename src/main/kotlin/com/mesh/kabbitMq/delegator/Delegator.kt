package com.mesh.kabbitMq.delegator

import io.ktor.util.logging.*
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties



/**
 * A delegate class to manage the state of properties, ensuring they are initialized before use.
 *
 * @param T the type of the property being delegated.
 *
 * @constructor Creates a `StateDelegator` with the initial state set to `Uninitialized`.
 *
 * @author Damir Denis-Tudor
 * @version 0.1.0
 */
internal class Delegator<T : Any>{

    private var state: State<T> = State.Uninitialized

    companion object {
        private val stateMap = mutableMapOf<Pair<String, String>, State<Any>>()

        private lateinit var ref: Any

        /**
         * Checks if all specified properties are initialized.
         *
         * @param properties the properties to check.
         * @param thisRef the object instance containing the properties.
         * @return true if all properties are initialized, false otherwise.
         */
        fun initialized(vararg properties: KProperty<*>, thisRef: Any = ref): Boolean {
            return properties.all {
                stateMap.getOrPut(thisRef.javaClass.simpleName to it.name) { State.Uninitialized } !is State.Uninitialized
            }
        }

        /**
         * Provides a trace of the states of all properties in the given object.
         * Returns a list of strings representing the state of each property.
         *
         * @param thisRef the object instance for which to trace property states.
         * @return a list of strings representing the state of each property.
         */
        fun stateTrace(thisRef: Any = ref): List<String> {
            return thisRef::class.memberProperties.map {
                val initialized = (stateMap[thisRef.javaClass.simpleName to it.name] is State.Initialized)
                "<${it.name}>, initialized: <$initialized>"
            }
        }

        /**
         * Logs a trace of the states of all properties of the given object to a logger.
         * This function calls `stateTrace()` to generate the state trace for the object and then logs it
         * using the `KtorSimpleLogger` for the object's class.
         *
         * If the state trace is generated, the log is output. If the combination of parameters is unsupported,
         * it throws an error with a message containing the class name of the object.
         *
         * @param thisRef the object instance for which to trace and log the state of its properties.
         *                Defaults to a reference object (`ref`), which can be replaced if needed.
         * @return message
         */
        fun reportStateTrace(thisRef: Any = ref): String {
            stateTrace().forEach {
                KtorSimpleLogger("io.kabbitmq.${thisRef.javaClass.simpleName}").debug(it)
            }
            return "Unsupported combination of parameters for ${thisRef.javaClass.simpleName}."
        }

        /**
         * Sets the reference of the current object and executes a block of code using the companion object.
         *
         * @param ref the reference to the current object.
         * @param block the block of code to execute with the companion object.
         */
        fun <T : Any> withThisRef(ref: Any, block: () -> T): T {
            KtorSimpleLogger("io.kabbitmq.${ref.javaClass.simpleName}")
                .debug("Build method for method called.")

            this.ref = ref

            val result = block.invoke()

            this.ref = Any()

            return result
        }
    }

    /**
     * Gets the value of the delegated property, throwing an exception if the property is not initialized.
     *
     * @param thisRef the object instance containing the property.
     * @param property the property to access.
     * @return the value of the property if initialized.
     * @throws UninitializedPropertyAccessException if the property is not initialized.
     */
    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        return when (val currentState = state) {
            is State.Initialized -> currentState.value
            else -> throw UninitializedPropertyAccessException(
                "Property <${property.javaClass}>: <${property.name}> must be initialized before accessing."
            )
        }
    }

    /**
     * Sets the value of the delegated property and updates its state to `Initialized`.
     *
     * @param thisRef the object instance containing the property.
     * @param property the property to set.
     * @param value the value to assign to the property.
     */
    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        state = State.Initialized(value)
        stateMap[thisRef.javaClass.simpleName to property.name] = State.Initialized(value)
    }
}
