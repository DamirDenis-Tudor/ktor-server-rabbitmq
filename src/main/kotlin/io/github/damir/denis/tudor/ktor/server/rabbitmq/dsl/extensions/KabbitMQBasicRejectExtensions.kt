package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQBasicRejectBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.basicReject
 */
@KabbitMQDslMarker
inline fun Application.basicReject(block: KabbitMQBasicRejectBuilder.() -> Unit) =
    KabbitMQBasicRejectBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.basicReject
 */
@KabbitMQDslMarker
inline fun Channel.basicReject(block: KabbitMQBasicRejectBuilder.() -> Unit) =
    KabbitMQBasicRejectBuilder(this).apply(block).build()
