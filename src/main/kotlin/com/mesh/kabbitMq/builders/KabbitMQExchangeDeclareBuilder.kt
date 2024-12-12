package com.mesh.kabbitMq.builders

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel

class KabbitMQExchangeDeclareBuilder(private val channel: Channel) {
    lateinit var exchange: String
    lateinit var type: BuiltinExchangeType
    var durable: Boolean = false
    var autoDelete: Boolean = false
    var internal: Boolean = false
    var arguments: Map<String, Any> = emptyMap()

    fun build() {
        channel.exchangeDeclare(exchange, type, durable, autoDelete, internal, arguments)
    }
}