package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions

import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQServiceKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.KabbitMQBasicQosBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import io.ktor.server.application.*

/**
 * @see Channel.basicQos
 */
@KabbitMQDslMarker
inline fun Application.basicQos(block: KabbitMQBasicQosBuilder.() -> Unit) =
    KabbitMQBasicQosBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()

/**
 * @see Channel.basicQos
 */
@KabbitMQDslMarker
inline fun Channel.basicQos(block: KabbitMQBasicQosBuilder.() -> Unit) =
    KabbitMQBasicQosBuilder(this).apply(block).build()