package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.initialized
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.reportStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.withThisRef
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQExchangeDeleteBuilder(private val channel: Channel) {
    var exchange: String by Delegator()
    var ifUnused: Boolean by Delegator()

    init {
        ifUnused = false
    }

    fun build(): AMQP.Exchange.DeleteOk = withThisRef(this@KabbitMQExchangeDeleteBuilder) {
        return@withThisRef when {
            initialized(::exchange, ::ifUnused) -> {
                channel.exchangeDelete(exchange, ifUnused)
            }

            else -> error(reportStateTrace())
        }
    }

}