package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.reportStateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel


@KabbitMQDslMarker
class KabbitMQConsumerCountBuilder(private val channel: Channel) {
    var queue: String by Delegator()

    fun build(): Long = withThisRef(this@KabbitMQConsumerCountBuilder)
    {
        return@withThisRef when {
            initialized(::queue) -> {
                channel.consumerCount(queue)
            }

            else -> error(reportStateTrace())
        }
    }
}