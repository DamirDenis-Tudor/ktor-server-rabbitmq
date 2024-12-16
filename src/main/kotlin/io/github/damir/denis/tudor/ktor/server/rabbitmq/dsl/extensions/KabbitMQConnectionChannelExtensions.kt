package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.service.KabbitMQConfig
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.ktor.server.application.*
import kotlin.random.Random

@KabbitMQDslMarker
inline fun Application.connection(id: String, autoClose: Boolean = false, block: Connection.() -> Unit) =
    attributes[KabbitMQServiceKey]
        .getConnection(id)
        .apply(block)
        .apply { if (autoClose) attributes[KabbitMQServiceKey].closeConnection(id) }

@KabbitMQDslMarker
inline fun Application.channel(id: Int, autoClose: Boolean = false, block: Channel.() -> Unit): Channel =
    attributes[KabbitMQServiceKey]
        .getChannel(id)
        .apply(block)
        .apply { if (autoClose) attributes[KabbitMQServiceKey].closeChannel(id) }

@KabbitMQDslMarker
inline fun Connection.channel(block: Channel.() -> Unit): Channel {
    val connectionId = KabbitMQConfig.service.getConnectionId(this)
    val channelId = Random.nextInt(100000, 100000000)
    return createChannel()
        .apply{ KabbitMQConfig.service.logConnectionChannel(channelId, connectionId) }
        .also(block)
        .apply { this.close().run { KabbitMQConfig.service.logChannelClosed(channelId) } }
}

@KabbitMQDslMarker
inline fun Connection.channel(id: Int, autoClose: Boolean = false, block: Channel.() -> Unit): Channel {
    val connectionId = KabbitMQConfig.service.getConnectionId(this)

    return KabbitMQConfig.service.getChannel(id, connectionId)
        .also(block)
        .apply { if (autoClose) KabbitMQConfig.service.closeChannel(id, connectionId) }
}