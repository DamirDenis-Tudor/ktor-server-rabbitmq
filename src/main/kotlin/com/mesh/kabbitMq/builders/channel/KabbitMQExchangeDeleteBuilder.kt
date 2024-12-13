package com.mesh.kabbitMq.builders.channel

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.Channel

@KabbitMQDslMarker
class KabbitMQExchangeDeleteBuilder(private val channel: Channel) {
    var exchange: String? = null
    var ifUnused: Boolean? = null

    fun build() {
        if (exchange != null) {
            channel.exchangeDelete(exchange!!, ifUnused ?: false)
        }
    }
}