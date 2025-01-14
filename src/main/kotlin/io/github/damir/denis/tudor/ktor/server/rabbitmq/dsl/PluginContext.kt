package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PluginContext(val connectionManager: ConnectionManager) : ChannelContext(connectionManager.getChannel())

@RabbitDslMarker
suspend fun PluginContext.channel(
    id: Int,
    autoClose: Boolean = false,
    block: suspend ChannelContext.() -> Unit
): Channel = withContext(Dispatchers.IO) {
    connectionManager.getChannel(id)
        .also {
            ChannelContext(it).apply { block() }
            if (autoClose) connectionManager.closeChannel(id)
        }
}

@RabbitDslMarker
suspend fun PluginContext.connection(
    id: String,
    autoClose: Boolean = false,
    block: suspend ConnectionContext.() -> Unit
): Connection = withContext(Dispatchers.IO) {
    connectionManager.getConnection(id)
        .also {
            ConnectionContext(connectionManager, it).apply { block() }
            if (autoClose) connectionManager.closeConnection(id)
        }
}

@RabbitDslMarker
suspend fun PluginContext.libConnection(
    id: String,
    autoClose: Boolean = false,
    block: suspend Connection.() -> Unit
): Connection = withContext(Dispatchers.IO) {
    connectionManager.getConnection(id)
        .also {
            it.apply { block() }
            if (autoClose) connectionManager.closeConnection(id)
        }
}
