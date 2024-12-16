package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQQueueDeclareBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.queueDeclare
 */
@KabbitMQDslMarker
inline fun Application.queueDeclare(block: KabbitMQQueueDeclareBuilder.() -> Unit) =
    KabbitMQQueueDeclareBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.queueDeclare
 */
@KabbitMQDslMarker
inline fun Channel.queueDeclare(block: KabbitMQQueueDeclareBuilder.() -> Unit) =
    KabbitMQQueueDeclareBuilder(this).apply(block).build()
