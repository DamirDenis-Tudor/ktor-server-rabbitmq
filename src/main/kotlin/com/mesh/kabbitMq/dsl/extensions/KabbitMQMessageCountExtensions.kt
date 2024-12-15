package com.mesh.kabbitMq.dsl.extensions

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQMessageCountBuilder
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.service.KabbitMQConfig
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