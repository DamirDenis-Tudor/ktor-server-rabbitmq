package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.reportStateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueUnbindBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var exchange: String by Delegator()
    var routingKey: String by Delegator()
    var arguments: Map<String, Any> by Delegator()

    fun build(): AMQP.Queue.UnbindOk = withThisRef(this@KabbitMQQueueUnbindBuilder) {
        return@withThisRef when {
            initialized(::queue, ::exchange, ::routingKey, ::arguments) -> {
                channel.queueUnbind(queue, exchange, routingKey, arguments)
            }

            initialized(::queue, ::exchange, ::routingKey) -> {
                channel.queueUnbind(queue, exchange, routingKey)
            }

            else -> error(reportStateTrace())
        }
    }
}