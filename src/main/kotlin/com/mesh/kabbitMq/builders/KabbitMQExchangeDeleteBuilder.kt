package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.mesh.kabbitMq.delegator.State
import com.mesh.kabbitMq.delegator.Delegator
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQExchangeDeleteBuilder(private val channel: Channel) {
    var exchange: String by Delegator()
    var ifUnused: Boolean by Delegator()

    init {
        ifUnused = false
    }

    fun build() {
        channel.exchangeDelete(exchange, ifUnused)
    }
}