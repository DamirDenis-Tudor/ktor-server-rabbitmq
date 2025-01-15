package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ConnectionContext(val connectionManager: ConnectionManager, val connection: Connection)

@RabbitDslMarker
inline fun ConnectionContext.channel(
    id: Int,
    autoClose: Boolean = false,
    crossinline block: ChannelContext.() -> Unit
): Channel {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = id
        return connectionManager.getChannel(channelId, connectionId)
            .also {
                ChannelContext(it).apply(block).apply {
                    if (autoClose) {
                        closeChannel(id)
                    }
                }
            }
    }
}

@RabbitDslMarker
inline fun ConnectionContext.channel(
    crossinline block: ChannelContext.() -> Unit
): Channel {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = Random.nextInt(100000, 100000000)
        return connectionManager.getChannel(channelId, connectionId)
            .also {
                ChannelContext(it).apply(block).apply {
                    closeChannel(channelId, connectionId)
                }
            }
    }
}

@RabbitDslMarker
inline fun ConnectionContext.libChannel(
    crossinline block: Channel.() -> Unit
): Channel {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = Random.nextInt(100000, 100000000)
        return connectionManager.getChannel(channelId, connectionId)
            .also {
                it.apply(block).apply {
                    closeChannel(channelId, connectionId)
                }
            }
    }
}