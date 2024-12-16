package io.github.damir.denis.tudor.ktor.server.rabbitmq.service

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import io.ktor.util.logging.*
import java.io.FileInputStream
import java.lang.Thread.sleep
import java.security.KeyStore
import java.util.concurrent.ConcurrentHashMap
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import kotlin.random.Random
import kotlin.system.exitProcess

/**
 * Service class that manages RabbitMQ connections and channels.
 * This class handles creating, caching, and managing RabbitMQ connections and channels.
 *
 * @param config the configuration to be used for creating connections.
 *
 * @author Damir Denis-Tudor
 * @version 0.1.0
 */
class KabbitMQService(private val config: KabbitMQConfig) {
    private val connectionFactory = ConnectionFactory()

    private val connectionCache = ConcurrentHashMap<String, Connection>()
    private val channelCache = ConcurrentHashMap<String, Channel>()

    private val logger = KtorSimpleLogger(this.javaClass.name)

    init {
        connectionFactory.apply {
            if(config.tlsEnabled)
                connectionFactory.apply { enableTLS() }
            setUri(config.uri)
        }

        when {
            logger.isDebugEnabled -> logger.debug("Debug mode is enabled.")
            logger.isTraceEnabled -> logger.debug("Trace mode is enabled.")

        }
    }

    /**
     * Configures TLS settings for RabbitMQ connection.
     */
    private fun ConnectionFactory.enableTLS() {
        run {
            val keyStore = KeyStore.getInstance("PKCS12").apply {
                load(
                    FileInputStream(config.tlsKeystorePath),
                    config.tlsKeystorePassword.toCharArray()
                )
            }

            val trustStore = KeyStore.getInstance("JKS").apply {
                load(
                    FileInputStream(config.tlsTruststorePath),
                    config.tlsTruststorePassword.toCharArray()
                )
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
    }


    private fun getChannelKey(connectionId: String, channelId: Int): String =
        "$connectionId-channel-$channelId"

    /**
     * Retries a block of code for a specified number of attempts with a delay between retries.
     *
     * @param block The block of code to execute. If it succeeds, the result will be returned.
     * @return The result of the block if it succeeds.
     * @throws  IllegalStateException if the block fails after the maximum number of retry attempts.
     */
    @Synchronized
    private fun <T> retry(block: () -> T): T {
        repeat(config.connectionAttempts) { index ->
            runCatching { block() }
                .onSuccess {
                    return@retry it
                }.onFailure {
                    logger.warn("${it.message}. Attempt ${index + 1} failed: ${it.message}.")
                    sleep(config.attemptDelay * 1000L)
                }
        }

        error("Failed after ${config.connectionAttempts} retries")
    }

    fun logConnectionChannel(channelId: Int, connectionId: String) =
        logger.trace("Created new channel with id <$channelId> for connection with id <$connectionId>.")

    fun logChannelClosed(channelId: Int) =
        logger.trace("Channel with id: <$channelId>, closed")

    /**
     * Retrieves a RabbitMQ connection by its ID. If the connection is not cached or is closed, a new one is created.
     *
     * @param id the ID of the connection to retrieve, defaults to "DEFAULT".
     * @return the requested RabbitMQ connection.
     */
    @Synchronized
    fun getConnection(id: String = config.defaultConnectionName): Connection = retry {
        if (connectionCache.containsKey(id)) {
            logger.trace("Connection with id: <{}> taken from cache.", id)
        }

        val connection = connectionCache.getOrPut(id) {
            logger.trace("Created new connection with id: <{}>.", id)
            connectionFactory.newConnection(id)
        }

        if (!connection.isOpen)
            error("Connection <$id> is not open.")

        return@retry connection
    }

    /**
     * Retrieves the ID of a RabbitMQ connection from the cache.
     * If the connection is found in the cache, its associated ID is returned.
     * If not found, the default ID `"default_connection"` is returned.
     *
     * @param connection The RabbitMQ connection whose ID is to be retrieved.
     * @return The ID of the connection, or `"default_connection"` if not found.
     */
    @Synchronized
    fun getConnectionId(connection: Connection): String =
        connectionCache.entries.find { it.value == connection }?.key ?: config.defaultConnectionName

    /**
     * Closes a RabbitMQ connection by its ID and removes it from the connection cache.
     *
     * @param connectionId the ID of the connection to close and remove.
     */
    @Synchronized
    fun closeConnection(connectionId: String) {
        connectionCache[connectionId]?.close()
        connectionCache.remove(connectionId)

        logger.trace("Connection with id: <{}>, closed", connectionId)
    }

    /**
     * Retrieves a RabbitMQ channel by its ID. If the channel is not cached or is closed, a new one is created.
     *
     * @param channelId the ID of the channel to retrieve, defaults to "DEFAULT".
     * @param connectionId the ID of the connection to use, defaults to "DEFAULT".
     * @return the requested RabbitMQ channel.
     */
    @Synchronized
    fun getChannel(channelId: Int = 1, connectionId: String = config.defaultConnectionName): Channel = retry {
        val id = getChannelKey(connectionId, channelId)

        if (channelCache.containsKey(id)) {
            logger.trace("Channel with id: <{}> taken from cache.", id)
        }

        val channel = channelCache.getOrPut(id) {
            val connection = getConnection(connectionId)
            logConnectionChannel(channelId, connectionId)
            connection.createChannel(channelId)
        }

        if (!channel.isOpen) {
            error("Channel <$channelId> is not open.")
        }

        return@retry channel
    }

    /**
     * Closes a RabbitMQ channel by its ID and removes it from the channel cache.
     *
     * @param channelId the ID of the channel to close and remove.
     */
    @Synchronized
    fun closeChannel(channelId: Int, connectionId: String = config.defaultConnectionName) {
        val id = getChannelKey(connectionId, channelId)

        channelCache[id]?.close()
        channelCache.remove(id)

        logChannelClosed(channelId)
    }
}
