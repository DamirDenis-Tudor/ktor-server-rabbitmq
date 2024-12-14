package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQQueueDeleteBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
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