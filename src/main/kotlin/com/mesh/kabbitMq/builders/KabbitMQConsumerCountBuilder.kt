package com.mesh.kabbitMq.builders

import com.rabbitmq.client.Channel

// Builder for consumerCount
class KabbitMQConsumerCountBuilder(private val channel: Channel) {
    lateinit var queue: String

    fun build(): Long {
        return channel.consumerCount(queue) as Long
    }
}