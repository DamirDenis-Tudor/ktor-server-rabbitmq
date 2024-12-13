package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.StateDelegator
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQConsumerCountBuilder(private val channel: Channel) {
    var queue: String by StateDelegator()

    fun build(): Long {
        return channel.consumerCount(queue)
    }
}