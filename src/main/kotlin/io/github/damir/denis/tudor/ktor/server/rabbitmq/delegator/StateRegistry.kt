package io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator

import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.logging.Logger
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

/**
 * StateRegistry is a utility object to manage and trace the initialization
 * states of properties in an object. It uses thread-local storage to manage
 * state and references for thread safety and separation.
 *
 * @version 1.1.4
 */
object StateRegistry {
    /**
     * Thread-local storage for the reference to the current object
     * and it's logger for the current thread's context.
     */
    private val ref = ThreadLocal<Any?>()
    private val logger = ThreadLocal<Logger>()

    /* Default logger for fallback. */
    private val defaultLogger = KtorSimpleLogger(this.javaClass.name)

    /**
     * Thread-local map to store the states of properties, identified by
     * the combination of object and property name.
     */
    private val states = ThreadLocal.withInitial {
        mutableMapOf<Pair<String, String>, State<Any>>()
    }

    /**
     * Adds a state for a given property.
     *
     * @param propertyOf the class name of the object.
     * @param propertyName the name of the property.
     * @param state the state to assign to the property.
     */
    fun addState(propertyOf: String, propertyName: String, state: State<Any>) {
        states.get()[propertyOf to propertyName] = state
    }

    /**
     * Executes a block of code within the context of a specific object (`delegatorScope`),
     * managing the reference and state map during execution.
     *
     * @param on the reference to the current object.
     * @param block the block of code to execute with the context of this object.
     */
    fun <T : Any> delegatorScope(on: Any, block: () -> T): T {
        ref.set(on)
        logger.set(KtorSimpleLogger(on.javaClass.name))

        return try {
            block()
        } finally {

            logger.set(defaultLogger)
            states.set(states.get().filter { it.key.first != ref.get()?.javaClass?.name }.toMutableMap())
            ref.remove()
        }
    }

    /**
     * Checks if all specified properties are initialized.
     *
     * @param properties the properties to check.
     * @return true if all properties are initialized, false otherwise.
     */
    fun verify(vararg properties: KProperty<*>): Boolean {
        ref.get()?.let { currentRef ->
            return properties.all {
                states.get()
                    .getOrPut(currentRef.javaClass.name to it.name) { State.Uninitialized } !is State.Uninitialized
            }
        } ?: error("No reference set for the current thread.")
    }

    /**
     * Provides a trace of the states of all properties in the given object.
     * Returns a list of strings representing the state and value of each property.
     *
     * @return a list of strings representing the state and value of each property.
     */
    fun stateTrace(): String {
        val currentRef = ref.get() ?: throw IllegalStateException("No reference set for the current thread.")
        return currentRef::class.memberProperties.joinToString("\n") {
            val state = states.get()[currentRef.javaClass.name to it.name]
            val initialized = (state is State.Initialized)
            val value = if (initialized) state.value else "Uninitialized"
            "<${ref.get()?.javaClass?.simpleName}> <${it.name}>, value: <$value>"
        }
    }
}
