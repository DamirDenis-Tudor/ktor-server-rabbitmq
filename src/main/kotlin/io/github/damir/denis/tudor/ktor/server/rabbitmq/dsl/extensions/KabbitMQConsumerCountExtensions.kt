package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQConsumerCountBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.consumerCount
 */
@KabbitMQDslMarker
inline fun Application.consumerCount(block: KabbitMQConsumerCountBuilder.() -> Unit) =
    KabbitMQConsumerCountBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.consumerCount
 */
@KabbitMQDslMarker
inline fun Channel.consumerCount(block: KabbitMQConsumerCountBuilder.() -> Unit) =
    KabbitMQConsumerCountBuilder(this).apply(block).build()