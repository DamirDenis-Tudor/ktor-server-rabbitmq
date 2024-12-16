package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.initialized
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.reportStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.withThisRef
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel


@KabbitMQDslMarker
class KabbitMQBasicAckBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegator()
    var multiple: Boolean by Delegator()

    init {
        multiple = false
    }

    fun build() = withThisRef(this@KabbitMQBasicAckBuilder) {
        return@withThisRef when {
            initialized(::deliveryTag, ::multiple) -> {
                channel.basicAck(deliveryTag, multiple)
            }

            else -> reportStateTrace()
        }
    }
}
