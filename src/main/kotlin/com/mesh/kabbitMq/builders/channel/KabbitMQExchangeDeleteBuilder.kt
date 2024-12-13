package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.util.State
import com.mesh.kabbitMq.util.StateDelegator
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQExchangeDeleteBuilder(private val channel: Channel) {
    var exchange: String by StateDelegator()
    var ifUnused: Boolean by StateDelegator(State.Initialized(false))

    fun build() {
        channel.exchangeDelete(exchange, ifUnused)
    }
}