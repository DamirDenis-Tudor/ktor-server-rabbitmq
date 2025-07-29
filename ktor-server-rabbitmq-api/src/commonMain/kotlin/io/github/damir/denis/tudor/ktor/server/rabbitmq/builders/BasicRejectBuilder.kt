package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.logStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel

@RabbitDslMarker
class BasicRejectBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegator()
    var requeue: Boolean by Delegator()

    init {
        requeue = false
    }

    fun build() = delegatorScope(on = this@BasicRejectBuilder) {
        return@delegatorScope when {
            verify(::deliveryTag, ::requeue) -> {
                channel.basicReject(deliveryTag, requeue)
            }

            else -> {
                logStateTrace()
                error("Unexpected combination of parameters")
            }
        }
    }
}
