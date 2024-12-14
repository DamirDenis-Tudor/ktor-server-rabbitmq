package com.mesh.kabbitMq.service

import io.ktor.server.config.*

/**
 * Configuration class for RabbitMQ settings.
 * This class loads configuration values for RabbitMQ URI and connection name from the provided Ktor application config.
 *
 * @param config the Ktor `ApplicationConfig` used to load configuration values.
 *
 * @author Damir Denis-Tudor
 * @version 0.1.0
 */
class KabbitMQConfig(config: ApplicationConfig) {
    var uri: String = config.tryGetString("uri") ?: "amqp://guest:guest@localhost:5672"
    var connectionName: String = config.tryGetString("connectionName") ?: "ktor"
}
