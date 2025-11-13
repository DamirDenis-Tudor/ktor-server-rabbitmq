package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.logStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.ExchangeDeleteOk

@RabbitDslMarker
class ExchangeDeleteBuilder(private val channel: Channel) {
    var exchange: String by Delegator()
    var ifUnused: Boolean by Delegator()

    init {
        ifUnused = false
    }

    suspend fun build(): ExchangeDeleteOk = delegatorScope(on = this@ExchangeDeleteBuilder) {
        return@delegatorScope when {
            verify(::exchange, ::ifUnused) -> {
                channel.exchangeDelete(exchange, ifUnused)
            }

            else -> {
                logStateTrace()
                error("Unexpected combination of parameters")
            }
        }
    }

}
