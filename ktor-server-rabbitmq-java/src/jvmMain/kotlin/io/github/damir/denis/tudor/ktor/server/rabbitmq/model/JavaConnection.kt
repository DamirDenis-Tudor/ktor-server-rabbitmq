package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

class JavaConnection(
    val connection: com.rabbitmq.client.Connection,
) : Connection {

    override val isOpen: Boolean
        get() = connection.isOpen

    override fun createChannel(): Channel? =
        connection.createChannel()?.let(::JavaChannel)

    override fun close() =
        connection.close()

}
