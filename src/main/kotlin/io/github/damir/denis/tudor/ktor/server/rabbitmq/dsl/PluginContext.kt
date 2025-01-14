package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager

class PluginContext(val connectionManager: ConnectionManager): ChannelContext(connectionManager.getChannel())

@RabbitDslMarker
fun PluginContext.channel(id: Int, autoClose: Boolean = false, block: ChannelContext.() -> Unit) {
    with(connectionManager) {
        ChannelContext(getChannel()).apply(block)
        if (autoClose) closeChannel(id)
    }
}

@RabbitDslMarker
fun PluginContext.connection(id: String, autoClose: Boolean = false, block: ConnectionContext.() -> Unit) {
    with(connectionManager) {
        ConnectionContext(this, getConnection(id)).apply(block)
        if (autoClose) closeConnection(id)
    }
}