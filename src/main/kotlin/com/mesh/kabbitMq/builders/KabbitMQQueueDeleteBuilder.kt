package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.State
import com.mesh.kabbitMq.util.StateDelegator
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueDeleteBuilder(private val channel: Channel) {
    var queue: String by StateDelegator()
    var ifUnused: Boolean by StateDelegator()
    var ifEmpty: Boolean by StateDelegator()

    fun build() {
        with(StateDelegator){
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
