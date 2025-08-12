package io.github.damir.denis.tudor.ktor.server.rabbitmq

import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.KourierConnectionManager

val RabbitMQ = createRabbitMQPlugin { application, pluginConfig ->
    KourierConnectionManager(application, pluginConfig)
}
