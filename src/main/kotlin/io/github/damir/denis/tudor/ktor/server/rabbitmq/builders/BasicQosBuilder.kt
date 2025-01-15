package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.delegatorScope
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.logStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.StateRegistry.verify
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker


@RabbitDslMarker
class BasicQosBuilder(private val channel: Channel) {
    var prefetchSize: Int by Delegator()
    var prefetchCount: Int by Delegator()
    var global: Boolean by Delegator()

    fun build() = delegatorScope(on = this@BasicQosBuilder) {
        return@delegatorScope when {
            verify(::prefetchCount, ::prefetchCount, ::global) -> {
                channel.basicQos(prefetchSize, prefetchCount, global)
            }

            verify(::prefetchCount, ::global) -> {
                channel.basicQos(prefetchCount, global)
            }

            verify(::prefetchCount) -> {
                channel.basicQos(prefetchCount)
            }

            else -> {
                logStateTrace()
                error("Unexpected combination of parameters")
            }
        }
    }
}