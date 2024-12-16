package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQQueueBindBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.queueBind
 */
@KabbitMQDslMarker
inline fun Application.queueBind(block: KabbitMQQueueBindBuilder.() -> Unit) =
    KabbitMQQueueBindBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.queueBind
 */
@KabbitMQDslMarker
inline fun Channel.queueBind(block: KabbitMQQueueBindBuilder.() -> Unit) =
    KabbitMQQueueBindBuilder(this).apply(block).build()