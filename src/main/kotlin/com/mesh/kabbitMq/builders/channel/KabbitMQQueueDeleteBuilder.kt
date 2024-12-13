package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQQueueDeleteBuilder(private val channel: Channel) {
    var queue: String? = null
    var ifUnused: Boolean? = null
    var ifEmpty: Boolean? = null

    fun build() {
        if (queue != null) {
            when {
                ifUnused != null && ifEmpty != null -> channel.queueDelete(queue!!, ifUnused!!, ifEmpty!!)
                else -> channel.queueDelete(queue!!)
            }
        }
    }
}