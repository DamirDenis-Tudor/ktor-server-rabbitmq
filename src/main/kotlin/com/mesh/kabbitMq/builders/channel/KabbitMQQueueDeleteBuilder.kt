package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.StateDelegator
import com.mesh.kabbitMq.util.StateDelegator.Companion.withThisRef
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueDeleteBuilder(private val channel: Channel) {
    var queue: String by StateDelegator()
    var ifUnused: Boolean by StateDelegator()
    var ifEmpty: Boolean by StateDelegator()

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
