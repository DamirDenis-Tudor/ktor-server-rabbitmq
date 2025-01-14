package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ConnectionContext(val connectionManager: ConnectionManager, val connection: Connection)

@RabbitDslMarker
suspend inline fun ConnectionContext.channel(
    id: Int,
    autoClose: Boolean = false,
    crossinline block: ChannelContext.() -> Unit
): Channel = withContext(Dispatchers.IO) {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = id
        return@withContext connectionManager.getChannel(channelId, connectionId)
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
suspend inline fun ConnectionContext.channel(
    crossinline block: ChannelContext.() -> Unit
): Channel = withContext(Dispatchers.IO) {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = Random.nextInt(100000, 100000000)
        return@withContext connectionManager.getChannel(channelId, connectionId)
            .also {
                ChannelContext(it).apply(block).apply {
                    closeChannel(channelId, connectionId)
                }
            }
    }
}

@RabbitDslMarker
suspend inline fun ConnectionContext.libChannel(
    crossinline block: Channel.() -> Unit
): Channel = withContext(Dispatchers.IO) {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = Random.nextInt(100000, 100000000)
        return@withContext connectionManager.getChannel(channelId, connectionId)
            .also {
                it.apply(block).apply {
                    closeChannel(channelId, connectionId)
                }
            }
    }
}