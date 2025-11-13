package io.github.damir.denis.tudor.ktor.server.rabbitmq.connection

import com.rabbitmq.client.ConnectionFactory
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.model.JavaConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.sync.withLock
import java.io.FileInputStream
import java.lang.Thread.sleep
import java.security.KeyStore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

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
open class JavaConnectionManager(
    private val scope: CoroutineScope,
    private val config: ConnectionConfig,
) : ConnectionManager() {

    private val connectionFactory = ConnectionFactory()

    private val executor = createExecutor()
    private val convertedDispatcher = executor.asCoroutineDispatcher()

    override val dispatcher
        get() = convertedDispatcher

    override val coroutineScope
        get() = config.scope ?: scope

    override val configuration
        get() = config

    init {
        connectionFactory.apply {
            if (config.tlsEnabled) enableTLS()
            setUri(config.uri)
            setSharedExecutor(executor)
            isAutomaticRecoveryEnabled = true;
        }
    }

    /**
     * Creates and returns an ExecutorService based on the provided configuration.
     *
     * This method checks the configuration to determine whether a cached thread pool
     * or a fixed-size thread pool should be used. The choice depends on the value of
     * `config.dispatcherThreadPollSize`. A custom `ThreadFactory` is used to name
     * threads uniquely and mark them as daemon threads, ensuring they don't block JVM shutdown.
     *
     * @return An ExecutorService either as a cached thread pool or fixed-size thread pool.
     */
    private fun createExecutor(): ExecutorService {
        val threadIdCounter = AtomicInteger(-1)
        val threadFactory = ThreadFactory { runnable ->
            val threadName = "rabbitMQ-${threadIdCounter.incrementAndGet()}"
            logger.debug("Creating new thread with ID <$threadName>")
            Thread(runnable, threadName).apply { isDaemon = true }
        }
        return if (config.dispatcherThreadPollSize == 0) {
            logger.debug("Creating newCachedThreadPool.")
            Executors.newCachedThreadPool(threadFactory)
        } else {
            logger.debug("Creating newFixedThreadPool with size ${config.dispatcherThreadPollSize}.")
            Executors.newFixedThreadPool(config.dispatcherThreadPollSize, threadFactory)
        }
    }

    /**
     * Enables TLS (Transport Layer Security) for RabbitMQ connections.
     *
     * This method loads the necessary keystore and truststore files, initializes SSLContext,
     * and configures the connection factory to use the secure protocol.
     */
    private fun ConnectionFactory.enableTLS() {
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
    }

    override suspend fun <T> retry(block: suspend () -> T): T {
        repeat(config.connectionAttempts) { index ->
            runCatching { block() }
                .onSuccess {
                    return@retry it
                }.onFailure {
                    when {
                        it is java.net.ConnectException -> {
                            logger.warn("Attempt ${index + 1} failed: $it.")
                            sleep(config.attemptDelay * 1000L)
                        }

                        else -> throw it
                    }
                }
        }

        throw InterruptedException("Failed after ${config.connectionAttempts} retries")
    }

    override suspend fun getConnection(id: String): Connection = connectionMutex.withLock {
        retry {
            if (connectionCache.containsKey(id)) logger.debug("Connection with id: <$id> taken from cache.")

            val connection = connectionCache.getOrPut(id) {
                logger.debug("Creating new connection with id: <$id>.")
                connectionFactory.newConnection(id)?.let(::JavaConnection)
                    ?: error("Connection with id <$id> was not created.")
            }

            if (!connection.isOpen) error("Connection <$id> is not open.")

            return@retry connection
        }
    }

}
