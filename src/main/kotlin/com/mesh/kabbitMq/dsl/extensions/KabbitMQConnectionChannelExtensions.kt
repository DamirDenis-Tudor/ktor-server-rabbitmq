package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.ktor.server.application.*

@KabbitMQDslMarker
inline fun Application.connection(id: String, autoClose: Boolean = true, block: Connection.() -> Unit) =
    attributes[KabbitMQServiceKey].getConnection(id).apply(block).apply{
        if (autoClose) attributes[KabbitMQServiceKey].closeConnection(id)
    }

@KabbitMQDslMarker
inline fun Application.channel(id: String, autoClose: Boolean = true, block: Channel.() -> Unit): Channel {
    return attributes[KabbitMQServiceKey].getChannel(id).apply(block).apply{
        if (autoClose) attributes[KabbitMQServiceKey].closeChannel(id)
    }
}

@KabbitMQDslMarker
inline fun Connection.channel(block: Channel.() -> Unit): Channel {
    return this.createChannel().also(block)
}

@KabbitMQDslMarker
inline fun Connection.channel(id: String, block: Channel.() -> Unit): Channel {
    return this.createChannel().also(block)
}