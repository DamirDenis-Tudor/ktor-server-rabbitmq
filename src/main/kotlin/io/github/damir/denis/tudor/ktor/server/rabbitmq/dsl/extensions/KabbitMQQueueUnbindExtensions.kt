package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQQueueUnbindBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.queueUnbind
 */
@KabbitMQDslMarker
inline fun Application.queueUnbind(block: KabbitMQQueueUnbindBuilder.() -> Unit) =
    KabbitMQQueueUnbindBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.queueUnbind
 */
@KabbitMQDslMarker
inline fun Channel.queueUnbind(block: KabbitMQQueueUnbindBuilder.() -> Unit) =
    KabbitMQQueueUnbindBuilder(this).apply(block).build()