package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQMessageCountBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.service.KabbitMQConfig
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.messageCount
 */
@KabbitMQDslMarker
inline fun Application.messageCount(block: KabbitMQMessageCountBuilder.() -> Unit) =
    KabbitMQMessageCountBuilder(KabbitMQConfig.service.getChannel()).apply(block).build()

/**
 * @see Channel.messageCount
 */
@KabbitMQDslMarker
inline fun Channel.messageCount(block: KabbitMQMessageCountBuilder.() -> Unit) =
    KabbitMQMessageCountBuilder(this).apply(block).build()