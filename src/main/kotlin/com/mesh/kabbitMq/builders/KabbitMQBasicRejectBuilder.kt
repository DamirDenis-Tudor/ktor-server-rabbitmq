package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.reportStateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
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