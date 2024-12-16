package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQBasicGetBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.basicGet
 */
@KabbitMQDslMarker
inline fun Channel.basicGet(block: KabbitMQBasicGetBuilder.() -> Unit) =
    KabbitMQBasicGetBuilder(this).apply(block).build()

/**
 * @see Channel.basicGet
 */
@KabbitMQDslMarker
inline fun Application.basicGet(block: KabbitMQBasicGetBuilder.() -> Unit) =
    KabbitMQBasicGetBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()
