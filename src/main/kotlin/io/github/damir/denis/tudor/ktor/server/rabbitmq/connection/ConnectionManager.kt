package io.github.damir.denis.tudor.ktor.server.rabbitmq.connection

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import io.ktor.util.logging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.io.FileInputStream
import java.lang.Thread.sleep
import java.security.KeyStore
import java.util.concurrent.ConcurrentHashMap
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
open class ConnectionManager(
    private val scope: CoroutineScope,
    private val config: ConnectionConfig
) {
    private val connectionFactory = ConnectionFactory()

    private val channelCache = ConcurrentHashMap<String, Channel>()
    private val connectionCache = ConcurrentHashMap<String, Connection>()

    private val logger = KtorSimpleLogger(this.javaClass.name)

    private val executor = createExecutor()
    private val convertedDispatcher = executor.asCoroutineDispatcher()

    val dispatcher
        get() = convertedDispatcher

    val coroutineScope
        get() = scope

    val configuration
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

    /**
     * Generates a unique key for identifying a channel within the channel cache.
     *
     * @param connectionId the ID of the connection.
     * @param channelId the ID of the channel.
     * @return a unique string key for the channel.
     */
    private fun getChannelKey(connectionId: String, channelId: Int): String =
        "$connectionId-channel-$channelId"

    /**
     * Retries a block of code a specified number of times with delays between attempts.
     *
     * @param block The block of code to execute. If it succeeds, the result is returned.
     * @return The result of the block if it succeeds.
     * @throws IllegalStateException if the block fails after the maximum number of retries.
     */
    @Synchronized
    private fun <T> retry(block: () -> T): T {
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

    /**
     * Retrieves or creates a RabbitMQ connection by its ID.
     *
     * If the connection is not found in the cache or is closed, a new connection is created.
     *
     * @param id the ID of the connection to retrieve. Defaults to the default connection name.
     * @return the RabbitMQ connection.
     */
    @Synchronized
    fun getConnection(id: String = config.defaultConnectionName): Connection = retry {
        if (connectionCache.containsKey(id)) {
            logger.debug("Connection with id: <$id> taken from cache.")
        }

        val connection = connectionCache.getOrPut(id) {
            logger.debug("Creating new connection with id: <$id>.")
            connectionFactory.newConnection(id) ?: error("Connection with id <$id> was not created.")
        }

        if (!connection.isOpen)
            error("Connection <$id> is not open.")

        return@retry connection
    }

    /**
     * Retrieves the ID of a connection from the cache.
     *
     * @param connection The RabbitMQ connection to identify.
     * @return The associated ID or the default connection name if not found.
     */
    @Synchronized
    fun getConnectionId(connection: Connection): String =
        connectionCache.entries.find { it.value == connection }?.key ?: config.defaultConnectionName

    /**
     * Closes and removes a RabbitMQ connection by its ID.
     *
     * @param connectionId the ID of the connection to close.
     */
    @Synchronized
    fun closeConnection(connectionId: String) {
        connectionCache[connectionId]?.close()
        connectionCache.remove(connectionId)

        logger.debug("Connection with id: <$connectionId>, closed")
    }

    /**
     * Retrieves or creates a RabbitMQ channel by its ID.
     *
     * If the channel is not found in the cache or is closed, a new one is created.
     *
     * @param channelId the ID of the channel to retrieve. Defaults to 1.
     * @param connectionId the ID of the connection to use. Defaults to the default connection name.
     * @return the RabbitMQ channel.
     */
    @Synchronized
    fun getChannel(channelId: Int = 1, connectionId: String = config.defaultConnectionName): Channel = retry {
        val id = getChannelKey(connectionId, channelId)

        if (channelCache.containsKey(id)) {
            logger.debug("Channel with id: <$id> will be taken from cache.")
        }

        val channel = channelCache.getOrPut(id) {
            logger.debug("Creating new channel with id <$channelId> for connection with id <$connectionId>.")
            getConnection(connectionId).createChannel() ?: error("Could not allocate this channel id <$channelId>. ")
        }

        if (!channel.isOpen) {
            channelCache.remove(id)
            error("Channel <$channelId> is not open. ${channel.closeReason.message}")
        }

        return@retry channel
    }

    /**
     * Closes and removes a RabbitMQ channel by its ID.
     *
     * @param channelId the ID of the channel to close.
     * @param connectionId the ID of the associated connection.
     */
    @Synchronized
    fun closeChannel(channelId: Int = 1, connectionId: String = config.defaultConnectionName) {
        val id = getChannelKey(connectionId, channelId)

        channelCache[id]?.close()
        channelCache.remove(id)

        logger.debug("Channel with id: <$channelId> for connection with id <$connectionId>, closed")
    }

    /**
     * Closes all active RabbitMQ connections.
     *
     * This method iterates through all connections in the connection cache and closes each one.
     */
    @Synchronized
    fun close() {
        connectionCache.values.forEach { connection -> connection.close() }
    }
}