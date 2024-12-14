package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQQueueUnbindBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
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