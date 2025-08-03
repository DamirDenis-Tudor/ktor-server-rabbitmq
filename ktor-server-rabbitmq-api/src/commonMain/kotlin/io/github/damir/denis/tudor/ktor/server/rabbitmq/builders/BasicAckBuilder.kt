package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.logStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel

@RabbitDslMarker
class BasicAckBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegator()
    var multiple: Boolean by Delegator()

    init {
        multiple = false
    }

    suspend fun build() = delegatorScope(on = this@BasicAckBuilder) {
        return@delegatorScope when {
            verify(::deliveryTag, ::multiple) -> {
                channel.basicAck(deliveryTag, multiple)
            }

            else -> {
                logStateTrace()
                error("Unexpected combination of parameters")
            }
        }
    }
}
