package com.mesh.kabbitMq

import com.mesh.kabbitMq.service.KabbitMQConfig
import com.mesh.kabbitMq.service.KabbitMQService
import io.ktor.server.application.*
import io.ktor.util.*

val KabbitMQServiceKey = AttributeKey<KabbitMQService>("KabbitMQService")

val KabbitMQ = createApplicationPlugin(
    name = "KabbitMQ",
    configurationPath = "rabbitmq",
    createConfiguration = ::KabbitMQConfig
) {
    with( KabbitMQService(pluginConfig)){
        application.attributes.put(KabbitMQServiceKey, this)
    }
}