package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import com.rabbitmq.client.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import kotlin.random.Random

class ConnectionContext(val connectionManager: ConnectionManager, val connection: Connection)

@RabbitDslMarker
inline fun ConnectionContext.channel(id: Int, autoClose: Boolean = false, block: ChannelContext.() -> Unit) {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = id

        ChannelContext(getChannel(channelId, connectionId))
            .apply(block)
            .apply {
                if (autoClose) {
                    closeChannel(id)
                }
            }
    }
}

@RabbitDslMarker
inline fun ConnectionContext.channel(block: ChannelContext.() -> Unit) {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = Random.nextInt(100000, 100000000)

        ChannelContext(getChannel(channelId, connectionId))
            .also(block)
            .apply { closeChannel(channelId) }
    }
}