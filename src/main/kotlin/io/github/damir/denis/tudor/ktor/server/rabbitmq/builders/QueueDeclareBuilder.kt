package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.logStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker

@RabbitDslMarker
class QueueDeclareBuilder(private val channel: Channel) {
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

    fun build(): AMQP.Queue.DeclareOk = delegatorScope(on = this@QueueDeclareBuilder) {
        return@delegatorScope when {
            verify(::queue, ::durable, ::exclusive, ::autoDelete, ::arguments) -> {
                channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments)
            }

            else -> {
                logStateTrace()
                error("Unexpected combination of parameters")
            }
        }
    }
}
