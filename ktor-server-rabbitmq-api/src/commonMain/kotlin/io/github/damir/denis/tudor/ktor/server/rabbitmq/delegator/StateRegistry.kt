package io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator

import io.ktor.util.logging.*
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KProperty

/**
 * StateRegistry is a utility object to manage and trace the initialization
 * states of properties in an object. It uses thread-local storage to manage
 * state and references for thread safety and separation.
 *
 * @version 1.1.4
 */
@ThreadLocal
object StateRegistry {
    /**
     * Thread-local storage for the reference to the current object
     * and it's logger for the current thread's context.
     */
    private var ref: Any? = null
    private var logger: Logger? = null

    /* Default logger for fallback. */
    private val defaultLogger = KtorSimpleLogger(this::class.qualifiedName!!)

    /**
     * Map to store the states of properties, identified by
     * the combination of object and property name.
     */
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
    suspend fun <T : Any> delegatorScope(on: Any, block: suspend () -> T): T {
        ref = on

        logger = KtorSimpleLogger(on::class.qualifiedName!!)
        logger!!.trace("DelegatorScope used for <${on::class.simpleName}>.")

        return try {
            block()
        } finally {
            logger = defaultLogger

            states = states.filter {
                it.key.first != ref!!::class.qualifiedName
            }.toMutableMap()

            ref = null
        }
    }

    /**
     * Checks if all specified properties are initialized.
     *
     * @param properties the properties to check.
     * @return true if all properties are initialized, false otherwise.
     */
    fun verify(vararg properties: KProperty<*>): Boolean {
        ref?.let { currentRef ->
            return properties.all {
                states.getOrPut(currentRef::class.qualifiedName!! to it.name) {
                    State.Uninitialized
                } !is State.Uninitialized
            }
        } ?: error("No reference set for the current thread.")
    }

    /**
     * Provides a trace of the states of all properties in the given object.
     * Returns a list of strings representing the state and value of each property.
     *
     * @return a list of strings representing the state and value of each property.
     */
    fun logStateTrace(vararg properties: KProperty<*> = emptyArray()) {
        val currentRef = ref ?: throw IllegalStateException("No reference set for the current thread.")
        val currentLogger = logger ?: defaultLogger

        currentLogger.trace("<${currentRef::class.simpleName}> State trace.")

        properties.map {
            val state = states[currentRef::class.qualifiedName to it.name]
            val value = if ((state is State.Initialized)) state.value else "Uninitialized"
            currentLogger.error("<${currentRef::class.simpleName}> <${it.name}>, value: <$value>")
        }
    }
}
