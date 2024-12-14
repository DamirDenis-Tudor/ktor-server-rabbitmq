package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueDeclareBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var durable: Boolean by Delegator()
    var exclusive: Boolean by Delegator()
    var autoDelete: Boolean by Delegator()
    var arguments: Map<String, Any> by Delegator()

    init {
        durable = true
        exclusive = false
        autoDelete = false
        arguments = emptyMap()
    }

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
