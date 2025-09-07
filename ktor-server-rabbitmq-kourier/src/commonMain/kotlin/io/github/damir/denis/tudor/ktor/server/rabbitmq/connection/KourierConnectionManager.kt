package io.github.damir.denis.tudor.ktor.server.rabbitmq.connection

import dev.kourier.amqp.AMQPException
import dev.kourier.amqp.connection.AMQPConfig
import dev.kourier.amqp.connection.amqpConfig
import dev.kourier.amqp.connection.createAMQPConnection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.KourierConnection
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withLock
import kotlinx.io.files.FileNotFoundException

/**
 * Manages RabbitMQ connections and channels with optional TLS support.
 *
 * This class is responsible for creating, caching, and managing RabbitMQ connections and channels.
 * It supports TLS configuration for secure communication and provides retry mechanisms for robust
 * connection handling.
 *
 * @param config the configuration object containing RabbitMQ connection settings.
 *
 * @author Damir Denis-Tudor
 * @version 1.0.0
 */
open class KourierConnectionManager(
    private val scope: CoroutineScope,
    private val config: ConnectionConfig,
) : ConnectionManager() {

    private val amqpConfig: AMQPConfig = amqpConfig(config.uri)

    override val dispatcher
        get() = Dispatchers.IO

    override val coroutineScope
        get() = config.scope ?: scope

    override val configuration
        get() = config

    init {
        if (config.tlsEnabled) enableTLS()
    }

    /**
     * Enables TLS (Transport Layer Security) for RabbitMQ connections.
     *
     * This method loads the necessary keystore and truststore files, initializes SSLContext,
     * and configures the connection factory to use the secure protocol.
     */
    private fun enableTLS() {
        // TODO
        throw FileNotFoundException("TLS is not yet implemented in KourierConnectionManager")
        /*
        val keyStore = KeyStore.getInstance("PKCS12").apply {
            load(FileInputStream(config.tlsKeystorePath), config.tlsKeystorePassword.toCharArray())
        }

        val trustStore = KeyStore.getInstance("JKS").apply {
            load(FileInputStream(config.tlsTruststorePath), config.tlsTruststorePassword.toCharArray())
        }

        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, config.tlsKeystorePassword.toCharArray())

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(trustStore)

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(keyManagerFactory.keyManagers, trustManagerFactory.trustManagers, null)
        useSslProtocol(sslContext)

        logger.debug("TLS enabled for RabbitMQ connection")
         */
    }

    override suspend fun <T> retry(block: suspend () -> T): T {
        repeat(config.connectionAttempts) { index ->
            runCatching { block() }
                .onSuccess {
                    return@retry it
                }.onFailure {
                    when {
                        it is AMQPException -> {
                            logger.warn("Attempt ${index + 1} failed: $it.")
                            delay(config.attemptDelay * 1000L)
                        }

                        else -> throw it
                    }
                }
        }

        throw CancellationException("Failed after ${config.connectionAttempts} retries")
    }

    override suspend fun getConnection(id: String): Connection = connectionMutex.withLock {
        retry {
            if (connectionCache.containsKey(id)) logger.debug("Connection with id: <$id> taken from cache.")

            val connection = connectionCache.getOrPut(id) {
                logger.debug("Creating new connection with id: <$id>.")
                createAMQPConnection(
                    coroutineScope,
                    amqpConfig.copy(server = amqpConfig.server.copy(connectionName = id))
                ).let(::KourierConnection)
            }

            if (!connection.isOpen) error("Connection <$id> is not open.")

            return@retry connection
        }
    }

}
