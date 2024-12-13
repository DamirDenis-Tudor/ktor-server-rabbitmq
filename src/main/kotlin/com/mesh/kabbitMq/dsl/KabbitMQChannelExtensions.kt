package com.mesh.kabbitMq.dsl

import com.mesh.kabbitMq.builders.channel.KabbitMQBasicConsumeBuilder
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection

@KabbitMQDslMarker
inline fun Connection.channel(id: String, block: Channel.() -> Unit): Channel =
    this.createChannel().also(block)

@KabbitMQDslMarker
inline fun Channel.basicConsume(block: KabbitMQBasicConsumeBuilder.() -> Unit) =
    KabbitMQBasicConsumeBuilder(this).apply(block).build()

