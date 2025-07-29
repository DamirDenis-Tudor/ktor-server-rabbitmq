package io.github.damir.denis.tudor.ktor.server.rabbitmq.connection

import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Connection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher

abstract class ConnectionManager {

    abstract val dispatcher: ExecutorCoroutineDispatcher
    abstract val coroutineScope: CoroutineScope
    abstract val configuration: ConnectionConfig

    abstract fun getConnection(id: String = configuration.defaultConnectionName): Connection
    abstract fun getConnectionId(connection: Connection): String
    abstract fun closeConnection(connectionId: String)

    abstract fun getChannel(channelId: Int = 1, connectionId: String = configuration.defaultConnectionName): Channel
    abstract fun closeChannel(channelId: Int = 1, connectionId: String = configuration.defaultConnectionName)

    abstract fun close()

}
