package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.Delegator
import com.rabbitmq.client.Channel


@KabbitMQDslMarker
class KabbitMQConsumerCountBuilder(private val channel: Channel) {
    var queue: String by Delegator()

    fun build(): Long = channel.consumerCount(queue)
}