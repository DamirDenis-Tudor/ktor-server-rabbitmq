package com.mesh.kabbitMq.builders.channel


import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueDeclareBuilder(private val channel: Channel) {
    lateinit var queue: String
    var durable: Boolean = true
    var exclusive: Boolean = false
    var autoDelete: Boolean = false
    var arguments: Map<String, Any> = emptyMap()

    fun build() = when{
        arguments.isEmpty() -> channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments)
        else -> channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments)
    }
}
