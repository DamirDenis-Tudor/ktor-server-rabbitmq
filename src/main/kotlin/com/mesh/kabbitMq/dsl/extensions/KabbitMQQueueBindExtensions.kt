package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQQueueBindBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
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