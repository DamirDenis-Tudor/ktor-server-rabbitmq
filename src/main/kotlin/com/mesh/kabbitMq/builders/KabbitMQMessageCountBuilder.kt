package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
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