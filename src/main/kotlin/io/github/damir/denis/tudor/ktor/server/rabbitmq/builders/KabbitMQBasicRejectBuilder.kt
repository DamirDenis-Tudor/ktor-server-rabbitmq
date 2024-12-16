package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.initialized
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.reportStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.withThisRef
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel


@KabbitMQDslMarker
class KabbitMQBasicRejectBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegator()
    var requeue: Boolean by Delegator()

    init {
        requeue = false
    }

    fun build() = withThisRef(this@KabbitMQBasicRejectBuilder) {
        return@withThisRef when {
            initialized(::deliveryTag, ::requeue) -> {
                channel.basicReject(deliveryTag, requeue)
            }

            else -> error(reportStateTrace())
        }
    }
}