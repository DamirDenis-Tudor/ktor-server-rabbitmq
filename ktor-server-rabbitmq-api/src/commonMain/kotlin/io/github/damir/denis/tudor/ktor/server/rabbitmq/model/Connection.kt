package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

interface Connection {

    val isOpen: Boolean

    fun createChannel(): Channel?

    fun close()

}
