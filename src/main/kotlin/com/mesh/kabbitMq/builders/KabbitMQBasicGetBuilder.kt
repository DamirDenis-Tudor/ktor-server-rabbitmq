package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel
import com.rabbitmq.client.GetResponse

@KabbitMQDslMarker
class KabbitMQBasicGetBuilder(private val channel: Channel) {
    lateinit var queue: String
    var autoAck: Boolean? = false

    fun build(): GetResponse = channel.basicGet(queue, autoAck ?: false)
}