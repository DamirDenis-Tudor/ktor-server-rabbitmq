package io.github.damir.denis.tudor.ktor.server.rabbitmq.builders

import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.initialized
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.reportStateTrace
import io.github.damir.denis.tudor.ktor.server.rabbitmq.delegator.Delegator.Companion.withThisRef
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel


@KabbitMQDslMarker
class KabbitMQBasicQosBuilder(private val channel: Channel) {
    var prefetchSize: Int by Delegator()
    var prefetchCount: Int by Delegator()
    var global: Boolean by Delegator()

    fun build() = withThisRef(this@KabbitMQBasicQosBuilder) {
        return@withThisRef when {
            initialized(::prefetchCount, ::prefetchCount, ::global) -> {
                channel.basicQos(prefetchSize, prefetchCount, global)
            }

            initialized(::prefetchCount, ::global) -> {
                channel.basicQos(prefetchCount, global)
            }

            initialized(::prefetchCount) -> {
                channel.basicQos(prefetchCount)
            }

            else -> error(reportStateTrace())
        }
    }
}