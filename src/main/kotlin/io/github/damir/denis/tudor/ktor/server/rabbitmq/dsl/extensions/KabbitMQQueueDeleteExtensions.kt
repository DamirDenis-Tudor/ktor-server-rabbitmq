package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQQueueDeleteBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.queueDelete
 */
@KabbitMQDslMarker
inline fun Application.queueDelete(block: KabbitMQQueueDeleteBuilder.() -> Unit) =
    KabbitMQQueueDeleteBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.queueDelete
 */
@KabbitMQDslMarker
inline fun Channel.queueDelete(block: KabbitMQQueueDeleteBuilder.() -> Unit) =
    KabbitMQQueueDeleteBuilder(this).apply(block).build()