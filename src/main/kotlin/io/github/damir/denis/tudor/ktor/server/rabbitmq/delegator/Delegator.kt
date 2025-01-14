package io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator

import com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory.propertyName
import io.ktor.util.logging.*
import io.ktor.util.logging.KtorSimpleLogger
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
internal class Delegator<T : Any> {

    private var state: State<T> = State.Uninitialized

    /**
     * Gets the value of the delegated property, throwing an exception if the property is not initialized.
     *
     * @param ref the object instance containing the property.
     * @param property the property to access.
     * @return the value of the property if initialized.
     * @throws UninitializedPropertyAccessException if the property is not initialized.
     */
    operator fun getValue(ref: Any, property: KProperty<*>): T {
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
        StateRegistry.addState(
            propertyOf = thisRef.javaClass.name,
            propertyName = property.name,
            state = state
        )
    }
}
