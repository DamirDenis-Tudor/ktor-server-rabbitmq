package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

class JavaConnection(
    val connection: com.rabbitmq.client.Connection,
) : Connection {

    override val isOpen: Boolean
        get() = connection.isOpen

    override suspend fun createChannel(): Channel? =
        connection.createChannel()?.let(::JavaChannel)

    override suspend fun close() =
        connection.close()

}
