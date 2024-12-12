package com.mesh.kabbitMq.builders

import com.rabbitmq.client.Channel

// Builder for exchangeDelete
class KabbitMQExchangeDeleteBuilder(private val channel: Channel) {
    var exchange: String? = null
    var ifUnused: Boolean? = null

    fun build() {
        if (exchange != null) {
            channel.exchangeDelete(exchange!!, ifUnused ?: false)
        }
    }
}