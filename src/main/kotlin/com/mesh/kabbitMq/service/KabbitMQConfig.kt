package com.mesh.kabbitMq.service

import io.ktor.server.config.*

class KabbitMQConfig(config: ApplicationConfig) {
    var uri: String = config.tryGetString("uri") ?: "amqp://guest:guest@localhost:5672"
    var connectionName: String = config.tryGetString("connectionName") ?: "ktor"
}