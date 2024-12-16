package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQBasicNackBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.basicNack
 */
@KabbitMQDslMarker
inline fun Application.basicNack(block: KabbitMQBasicNackBuilder.() -> Unit) =
    KabbitMQBasicNackBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.basicNack
 */
@KabbitMQDslMarker
inline fun Channel.basicNack(block: KabbitMQBasicNackBuilder.() -> Unit) =
    KabbitMQBasicNackBuilder(this).apply(block).build()