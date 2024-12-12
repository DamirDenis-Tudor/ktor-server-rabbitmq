package com.mesh.kabbitMq.builders

import com.rabbitmq.client.Channel

// Builder for queueDelete
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