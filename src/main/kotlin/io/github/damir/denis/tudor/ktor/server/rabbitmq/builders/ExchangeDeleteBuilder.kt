package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.stateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker

@RabbitDslMarker
class ExchangeDeleteBuilder(private val channel: Channel) {
    var exchange: String by Delegator()
    var ifUnused: Boolean by Delegator()

    init {
        ifUnused = false
    }

    fun build(): AMQP.Exchange.DeleteOk = delegatorScope(on = this@ExchangeDeleteBuilder) {
        return@delegatorScope when {
            verify(::exchange, ::ifUnused) -> {
                channel.exchangeDelete(exchange, ifUnused)
            }

            else -> error(stateTrace())
        }
    }

}