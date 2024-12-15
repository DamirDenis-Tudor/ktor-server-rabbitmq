package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.reportStateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
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