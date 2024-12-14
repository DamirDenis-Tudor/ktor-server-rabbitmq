package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueDeleteBuilder(private val channel: Channel) {
    var queue: String by Delegator()
    var ifUnused: Boolean by Delegator()
    var ifEmpty: Boolean by Delegator()

    fun build() {
        withThisRef(this@KabbitMQQueueDeleteBuilder){
            when {
                initialized(::ifUnused, ::ifEmpty) -> {
                    channel.queueDelete(queue, ifUnused, ifEmpty)
                }
                else -> {
                    channel.queueDelete(queue)
                }
            }
        }
    }
}
