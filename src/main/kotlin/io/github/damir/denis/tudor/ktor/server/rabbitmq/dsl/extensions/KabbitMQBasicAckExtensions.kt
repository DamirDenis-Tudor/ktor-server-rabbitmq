package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQBasicAckBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.basicAck
 */
@KabbitMQDslMarker
inline fun Channel.basicAck(block: KabbitMQBasicAckBuilder.() -> Unit) =
    KabbitMQBasicAckBuilder(this).apply(block).build()

/**
 * @see Channel.basicAck
 */
@KabbitMQDslMarker
inline fun Application.basicAck(block: KabbitMQBasicAckBuilder.() -> Unit) =
    KabbitMQBasicAckBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()
