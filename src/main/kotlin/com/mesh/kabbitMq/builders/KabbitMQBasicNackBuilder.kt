package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.reportStateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQBasicNackBuilder(private val channel: Channel) {
    var deliveryTag: Long by Delegator()
    var multiple: Boolean by Delegator()
    var requeue: Boolean by Delegator()

    init {
        multiple = false
        requeue = false
    }

    fun build() = withThisRef(this@KabbitMQBasicNackBuilder) {
        return@withThisRef when {
            initialized(::deliveryTag, ::multiple, ::requeue) -> {
                channel.basicNack(deliveryTag, multiple, requeue)
            }

            else -> error(reportStateTrace())
        }
    }
}