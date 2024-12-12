package com.mesh.kabbitMq.builders

import com.rabbitmq.client.Channel

class KabbitMQQueueBindBuilder(private val channel: Channel) {
    lateinit var queue: String
    lateinit var exchange: String
    lateinit var routingKey: String
    var arguments: Map<String, Any> = emptyMap()

    fun build() {
        channel.queueBind(queue, exchange, routingKey, arguments)
    }
}