package io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator

/**
 * Exception thrown when attempting to access a property that has not been initialized.
 *
 * @param message the detail message for the exception.
 */
class UninitializedPropertyException(
    message: String,
) : RuntimeException(message)
