package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQConsumerCountBuilder(private val channel: Channel) {
    lateinit var queue: String

    fun build(): Long {
        return channel.consumerCount(queue) as Long
    }
}