package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ConnectionContext(val connectionManager: ConnectionManager, val connection: Connection)

@RabbitDslMarker
inline fun ConnectionContext.channel(
    id: Int,
    autoClose: Boolean = false,
    crossinline block: ChannelContext.() -> Unit
) = runCatching {
    with(connectionManager) {
        coroutineScope.launch(dispatcher) {
            connectionManager.getChannel(id, getConnectionId(connection))
                .also {
                    ChannelContext(connectionManager, it).apply(block).apply {
                        if (autoClose) {
                            closeChannel(id)
                        }
                    }
                }
        }
    }
}

@RabbitDslMarker
inline fun ConnectionContext.channel(
    autoClose: Boolean = false,
    crossinline block: ChannelContext.() -> Unit
) = runCatching {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = Random.nextInt(1000, 5000)
        coroutineScope.launch(dispatcher) {
            connectionManager.getChannel(channelId, connectionId)
                .also {
                    ChannelContext(connectionManager, it).apply(block).apply {
                        if (autoClose) {
                            closeChannel(channelId, connectionId)
                        }
                    }
                }
        }
    }
}

@RabbitDslMarker
inline fun ConnectionContext.libChannel(
    autoClose: Boolean = false,
    crossinline block: suspend Channel.() -> Unit
) = runCatching {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = Random.nextInt(1000, 5000)
        coroutineScope.launch(dispatcher) {
            connectionManager.getChannel(channelId, connectionId)
                .also {
                    it.apply{ this.block() }.apply {
                        if (autoClose) {
                            closeChannel(channelId, connectionId)
                        }
                    }
                }
        }
    }
}