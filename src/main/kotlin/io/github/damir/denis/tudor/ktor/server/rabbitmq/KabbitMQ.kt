package io.github.damir.denis.tudor.ktor.server.rabbitmq

import io.github.damir.denis.tudor.ktor.server.rabbitmq.service.KabbitMQConfig
import io.github.damir.denis.tudor.ktor.server.rabbitmq.service.KabbitMQService
import io.ktor.server.application.*
import io.ktor.util.*

val KabbitMQServiceKey = AttributeKey<KabbitMQService>("KabbitMQService")

val KabbitMQ = createApplicationPlugin(
    name = "KabbitMQ",
    configurationPath = "ktor.rabbitmq",
    createConfiguration = ::KabbitMQConfig
) {
    with( KabbitMQService(pluginConfig)){
        KabbitMQConfig.service = this
        application.attributes.put(KabbitMQServiceKey, this)
    }
}