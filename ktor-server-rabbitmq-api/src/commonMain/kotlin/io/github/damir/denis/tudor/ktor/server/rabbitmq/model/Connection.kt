package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

interface Connection {

    val isOpen: Boolean

    suspend fun createChannel(): Channel?

    suspend fun close()

}
