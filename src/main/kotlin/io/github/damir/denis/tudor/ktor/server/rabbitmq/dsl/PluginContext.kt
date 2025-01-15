package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PluginContext(val connectionManager: ConnectionManager) : ChannelContext(connectionManager.getChannel())

@RabbitDslMarker
fun PluginContext.channel(
    id: Int,
    autoClose: Boolean = false,
    block: ChannelContext.() -> Unit
): Channel {
    return connectionManager.getChannel(id)
        .also {
            ChannelContext(it).apply { block() }
            if (autoClose) connectionManager.closeChannel(id)
        }
}

@RabbitDslMarker
fun PluginContext.connection(
    id: String,
    autoClose: Boolean = false,
    block: ConnectionContext.() -> Unit
): Connection {
    return connectionManager.getConnection(id)
        .also {
            ConnectionContext(connectionManager, it).apply { block() }
            if (autoClose) connectionManager.closeConnection(id)
        }
}

@RabbitDslMarker
fun PluginContext.libConnection(
    id: String,
    autoClose: Boolean = false,
    block: Connection.() -> Unit
): Connection  {
    return connectionManager.getConnection(id)
        .also {
            it.apply { block() }
            if (autoClose) connectionManager.closeConnection(id)
        }
}
