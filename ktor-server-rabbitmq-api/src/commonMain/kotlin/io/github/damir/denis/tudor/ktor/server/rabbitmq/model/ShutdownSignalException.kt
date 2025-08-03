package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

data class ShutdownSignalException(
    val hardError: Boolean = false,
    val initiatedByApplication: Boolean = false,
    val reason: Any? = null,
    val ref: Any? = null,
)
