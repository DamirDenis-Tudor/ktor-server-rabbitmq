package com.mesh.kabbitMq.builders

import com.rabbitmq.client.Channel

class KabbitMQBasicGetBuilder(private val channel: Channel) {
    lateinit var queue: String
    var autoAck: Boolean? = false

    fun build() {
            channel.basicGet(queue, autoAck ?: false)
    }
}