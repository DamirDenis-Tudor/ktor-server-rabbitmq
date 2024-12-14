package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.State
import com.mesh.kabbitMq.util.StateDelegator
import com.mesh.kabbitMq.util.StateDelegator.Companion.withThisRef
import com.rabbitmq.client.AMQP.Queue.DeclareOk
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueDeclareBuilder(private val channel: Channel) {
    var queue: String by StateDelegator()
    var durable: Boolean = true
    var exclusive: Boolean = false
    var autoDelete: Boolean = false
    var arguments: Map<String, Any> = emptyMap()

    fun build(): Unit = withThisRef(this@KabbitMQQueueDeclareBuilder) {
        when {
            initialized(::queue) -> {
                channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments)
            }
            else -> {
                channel.queueDeclare()
            }
        }
    }
}
