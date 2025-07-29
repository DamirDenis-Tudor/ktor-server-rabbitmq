package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.logStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.QueueBindOk

@RabbitDslMarker
class QueueBindBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var exchange: String by Delegator()
    var routingKey: String by Delegator()
    var arguments: Map<String, Any> by Delegator()

    init {
        routingKey = ""
    }

    fun build(): QueueBindOk = delegatorScope(on = this@QueueBindBuilder) {
        return@delegatorScope when {
            verify(::queue, ::exchange, ::routingKey, ::arguments) -> {
                channel.queueBind(queue, exchange, routingKey, arguments)
            }

            verify(::queue, ::exchange, ::routingKey) -> {
                channel.queueBind(queue, exchange, routingKey)
            }

            else -> {
                logStateTrace()
                error("Unexpected combination of parameters")
            }
        }
    }
}
