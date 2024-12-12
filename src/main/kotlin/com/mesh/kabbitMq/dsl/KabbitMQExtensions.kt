package com.mesh.kabbitMq.dsl

import com.mesh.kabbitMq.KabbitMQServiceKey
import com.mesh.kabbitMq.builders.KabbitMQBasicAckBuilder
import io.ktor.server.application.*

inline fun Application.basicAck(block: KabbitMQBasicAckBuilder.() -> Unit) =
    KabbitMQBasicAckBuilder(attributes[KabbitMQServiceKey].getChannel()).apply(block).build()
