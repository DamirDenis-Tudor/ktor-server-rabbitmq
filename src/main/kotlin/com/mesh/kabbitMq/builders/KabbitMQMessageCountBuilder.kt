package com.mesh.kabbitMq.builders

import com.rabbitmq.client.Channel

// Builder for messageCount
class KabbitMQMessageCountBuilder(private val channel: Channel) {
    var queue: String? = null

    fun build(): Long {
        return if (queue != null) {
            channel.messageCount(queue!!)
        } else {
            0L
        }
    }
}