package io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator

import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.logging.Logger
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

/**
 * StateRegistry is a utility object to manage and trace the initialization
 * states of properties in an object. It uses a map to keep track of
 * the initialization state of each property and provides methods
 * to verify, trace, and manipulate these states.
 *
 * @author Damir Denis-Tudor
 * @version 1.1.3
 */
object StateRegistry {
    /* Holds a reference to the current object for when the delegate scope is being called */
    private lateinit var ref: Any

    /* Logger for debugging purposes. */
    private lateinit var logger: Logger
    private val default = KtorSimpleLogger(this.javaClass.name)

    /* Map to store the state of properties, identified by the combination of object and property name. */
    private var states = mutableMapOf<Pair<String, String>, State<Any>>()

    /**
     * Adds a state for a given property.
     *
     * @param propertyOf the class name of the object.
     * @param propertyName the name of the property.
     * @param state the state to assign to the property.
     */
    fun addState(propertyOf: String, propertyName: String, state: State<Any>) {
        states[propertyOf to propertyName] = state
    }

    /**
     * Executes a block of code within the context of a specific object (`delegatorScope`),
     * managing the reference and state map during execution.
     *
     * @param on the reference to the current object.
     * @param block the block of code to execute with the context of this object.
     */
    fun <T : Any> delegatorScope(on: Any, block: () -> T): T {
        this.ref = on
        this.logger = KtorSimpleLogger(on.javaClass.name)

        val result = block.invoke()

        // Reset the reference and logger after block execution.
        this.ref = Any()
        this.logger = default

        // Remove states related to the current object.
        states = states.filter { it.key.first != on.javaClass.name }
            .toMutableMap()

        return result
    }

    /**
     * Checks if all specified properties are initialized.
     *
     * @param properties the properties to check.
     * @return true if all properties are initialized, false otherwise.
     */
    fun verify(vararg properties: KProperty<*>): Boolean {
        return properties.all {
            states.getOrPut(ref.javaClass.simpleName to it.name) { State.Uninitialized } !is State.Uninitialized
        }
    }

    /**
     * Provides a trace of the states of all properties in the given object.
     * Returns a list of strings representing the state and value of each property.
     *
     * @return a list of strings representing the state and value of each property.
     */
    fun stateTrace(): List<String> {
        return ref::class.memberProperties.map {
            val state = states[ref.javaClass.simpleName to it.name]
            val initialized = (state is State.Initialized)
            val value = if (initialized) state.value else "Uninitialized"
            "<${it.name}>, initialized: <$initialized>, value: <$value>"
        }
    }
}
