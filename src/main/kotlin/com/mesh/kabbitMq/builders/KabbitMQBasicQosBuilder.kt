package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.rabbitmq.client.Channel


@KabbitMQDslMarker
class KabbitMQBasicQosBuilder(private val channel: Channel) {
    var prefetchSize: Int by Delegator()
    var prefetchCount: Int by Delegator()
    var global: Boolean by Delegator()

    fun build() {
        withThisRef(this@KabbitMQBasicQosBuilder){
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