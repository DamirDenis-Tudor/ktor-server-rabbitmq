package io.github.damir.denis.tudor.ktor.server.rabbitmq.connection

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
class ConnectionConfig(config: ApplicationConfig) {
    companion object{
        lateinit var service: ConnectionManager
    }
    var uri: String = config.tryGetString("uri") ?: "amqp://guest:guest@localhost:5672"
    var defaultConnectionName: String = config.tryGetString("defaultConnectionName") ?: "default_connection"
    var connectionAttempts: Int = config.tryGetString("connectionAttempts")?.toInt() ?: 10
    var attemptDelay: Int = config.tryGetString("attemptDelay")?.toInt() ?: 5
    var dispatcherThreadPollSize: Int = config.tryGetString("dispatcherThreadPollSize")?.toInt() ?: 8

    var tlsEnabled: Boolean = config.tryGetString("tls.enabled")?.toBoolean() == true
    var tlsKeystorePath: String = config.tryGetString("tls.keystorePath") ?: ""
    var tlsKeystorePassword: String = config.tryGetString("tls.keystorePassword") ?: ""
    var tlsTruststorePath: String = config.tryGetString("tls.truststorePath") ?: ""
    var tlsTruststorePassword: String = config.tryGetString("tls.truststorePassword") ?: ""

    fun verify(){
        require(connectionAttempts > 0) { "connectionAttempts must be > 0" }
        require(attemptDelay > 0) { "attemptDelay must be > 0" }
        require(dispatcherThreadPollSize > 1) { "dispatcherThreadPollSize must be > 1" }
        require(defaultConnectionName.isNotEmpty()) { "defaultConnectionName cannot be empty" }
        require(uri.isNotEmpty()) { "uri cannot be empty" }
        if (tlsEnabled){
            require(tlsKeystorePath.isNotEmpty()){ "tlsKeystorePath cannot be empty, tlsEnabled: $tlsEnabled" }
            require(tlsKeystorePassword.isNotEmpty()){ "tlsKeystorePassword cannot be empty, tlsEnabled: $tlsEnabled" }
            require(tlsTruststorePath.isNotEmpty()){ "tlsTruststorePath cannot be empty, tlsEnabled: $tlsEnabled" }
            require(tlsTruststorePassword.isNotEmpty()){ "tlsTruststorePassword cannot be empty, tlsEnabled: $tlsEnabled" }
        }
    }
}
