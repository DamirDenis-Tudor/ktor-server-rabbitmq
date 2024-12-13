package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.StateDelegator
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQBasicQosBuilder(private val channel: Channel) {
    var prefetchSize: Int by StateDelegator()
    var prefetchCount: Int by StateDelegator()
    var global: Boolean by StateDelegator()

    fun build() {
        with(StateDelegator) {
            when {
                initialized(::prefetchCount, ::global) -> {
                    channel.basicQos(prefetchSize, prefetchCount, global )
                }
                initialized(::prefetchCount) -> {
                    channel.basicQos(prefetchCount, global)
                }
                else -> {
                    channel.basicQos(prefetchCount)
                }
            }
        }
    }
}