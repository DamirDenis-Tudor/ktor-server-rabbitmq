package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.stateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker

@KabbitMQDslMarker
class KabbitMQQueueBindBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var exchange: String by Delegator()
    var routingKey: String by Delegator()
    var arguments: Map<String, Any> by Delegator()

    init {
        routingKey = ""
    }

    fun build(): AMQP.Queue.BindOk = delegatorScope(on = this@KabbitMQQueueBindBuilder) {
        return@delegatorScope when {
            verify(::queue, ::exchange, ::routingKey,::arguments) -> {
                channel.queueBind(queue, exchange, routingKey, arguments)
            }

            verify(::queue, ::exchange, ::routingKey) -> {
                channel.queueBind(queue, exchange, routingKey)
            }

            else -> error(stateTrace())
        }
    }
}