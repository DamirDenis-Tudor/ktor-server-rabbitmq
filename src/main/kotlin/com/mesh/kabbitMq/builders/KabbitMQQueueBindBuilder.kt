package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.stateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import io.ktor.util.logging.*

@KabbitMQDslMarker
class KabbitMQQueueBindBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var exchange: String by Delegator()
    var routingKey: String by Delegator()
    var arguments: Map<String, Any> by Delegator()

    init {
        routingKey = ""
    }

    fun build(): AMQP.Queue.BindOk = withThisRef(this@KabbitMQQueueBindBuilder) {
        return@withThisRef when {
            initialized(::queue, ::exchange, ::routingKey,::arguments) -> {
                channel.queueBind(queue, exchange, routingKey, arguments)
            }

            initialized(::queue, ::exchange, ::routingKey) -> {
                channel.queueBind(queue, exchange, routingKey)
            }

            else -> {
                stateTrace().forEach { KtorSimpleLogger("KabbitMQQueueBindBuilder").warn(it) }
                error("Unsupported combination of parameters for basicConsume.")
            }
        }
    }
}