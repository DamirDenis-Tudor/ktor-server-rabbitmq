package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQQueueDeclareBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
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
