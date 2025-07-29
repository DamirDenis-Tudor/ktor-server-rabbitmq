package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.logStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Channel

@RabbitDslMarker
class BasicQosBuilder(private val channel: Channel) {
    var prefetchSize: Int by Delegator()
    var prefetchCount: Int by Delegator()
    var global: Boolean by Delegator()

    suspend fun build() = delegatorScope(on = this@BasicQosBuilder) {
        return@delegatorScope when {
            verify(::prefetchCount, ::prefetchCount, ::global) -> {
                channel.basicQos(prefetchSize, prefetchCount, global)
            }

            verify(::prefetchCount, ::global) -> {
                channel.basicQos(
                    prefetchCount = prefetchCount,
                    global = global
                )
            }

            verify(::prefetchCount) -> {
                channel.basicQos(
                    prefetchCount = prefetchCount
                )
            }

            else -> {
                logStateTrace()
                error("Unexpected combination of parameters")
            }
        }
    }
}
