package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

import dev.kourier.amqp.connection.AMQPConnection
import dev.kourier.amqp.connection.ConnectionState

class KourierConnection(
    val connection: AMQPConnection,
) : Connection {

    override val isOpen: Boolean
        get() = connection.state == ConnectionState.OPEN

    override suspend fun createChannel(): Channel? =
        connection.openChannel().let(::KourierChannel)

    override suspend fun close() =
        connection.close().let {}

}
