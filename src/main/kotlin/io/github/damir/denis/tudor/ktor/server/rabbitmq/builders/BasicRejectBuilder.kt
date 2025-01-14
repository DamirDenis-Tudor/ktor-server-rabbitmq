package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.stateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker


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

            else -> error(stateTrace())
        }
    }
}