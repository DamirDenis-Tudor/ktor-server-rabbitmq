package com.mesh.kabbitMq.builders

import com.rabbitmq.client.Channel
import kotlin.properties.Delegates

class KabbitMQBasicQosBuilder(private val channel: Channel) {
    var prefetchSize: Int? = null
    var prefetchCount by Delegates.notNull<Int>()
    var global: Boolean? = null

    fun build() {
        when {
            prefetchSize != null -> channel.basicQos(prefetchSize!!, prefetchCount, global ?: false)
            global != null -> channel.basicQos(prefetchCount, global ?: false)
            else -> channel.basicQos(prefetchCount)
        }
    }
}