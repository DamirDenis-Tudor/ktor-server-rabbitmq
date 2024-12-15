package com.mesh.kabbitMq.builders

import com.mesh.kabbitMq.delegator.Delegator
import com.mesh.kabbitMq.delegator.Delegator.Companion.initialized
import com.mesh.kabbitMq.delegator.Delegator.Companion.stateTrace
import com.mesh.kabbitMq.delegator.Delegator.Companion.withThisRef
import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import io.ktor.util.logging.*

@KabbitMQDslMarker
class KabbitMQExchangeDeleteBuilder(private val channel: Channel) {
    var exchange: String by Delegator()
    var ifUnused: Boolean by Delegator()

    init {
        ifUnused = false
    }

    fun build(): AMQP.Exchange.DeleteOk = withThisRef(this@KabbitMQExchangeDeleteBuilder) {
        return@withThisRef when {
            initialized(::exchange, ::ifUnused) -> {
                channel.exchangeDelete(exchange, ifUnused)
            }

            else -> {
                stateTrace().forEach { KtorSimpleLogger("KabbitMQExchangeDeleteBuilder").warn(it) }
                error("Unsupported combination of parameters for basicConsume.")
            }
        }
    }

}