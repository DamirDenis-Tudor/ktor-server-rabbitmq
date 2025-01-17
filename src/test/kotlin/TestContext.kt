import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import io.github.damir.denis.tudor.ktor.server.rabbitmq.ConnectionManagerKey
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.ChannelContext
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.ConnectionContext
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.PluginContext
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.RabbitDslMarker
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.getChannelContext
import io.github.damir.denis.tudor.ktor.server.rabbitmq.rabbitMQ
import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

fun Application.rabbitmqTest(block: suspend PluginContext.() -> Unit) = runBlocking {
    with(attributes[ConnectionManagerKey]) {
        coroutineScope.launch(Dispatchers.rabbitMQ) {
            PluginContext(this@with).apply { this.block() }
        }.join()
    }
}

fun PluginContext.channelTest(block: suspend PluginContext.() -> Unit) = runBlocking {
    with(connectionManager) {
        getChannel().also {
            coroutineScope.launch(Dispatchers.rabbitMQ) {
                getChannelContext(it).apply { block() }
            }.join()
        }
    }
}

@RabbitDslMarker
fun PluginContext.channelTest(
    id: Int,
    autoClose: Boolean = false,
    block: suspend ChannelContext.() -> Unit
) = runBlocking {
    with(connectionManager) {
        getChannel(id).also {
            coroutineScope.launch(Dispatchers.rabbitMQ) {
                getChannelContext(it).apply {
                    block()
                }
            }.also { job ->
                if (autoClose) {
                    job.join()
                    closeChannel(id)
                }
            }.join()
        }
    }
}

@RabbitDslMarker
fun PluginContext.libChannelTest(
    id: Int,
    autoClose: Boolean = false,
    block: suspend Channel.() -> Unit
) = runBlocking {
    with(connectionManager) {
        getChannel(id).also {
            coroutineScope.launch(Dispatchers.rabbitMQ) {
                it.apply { block() }
            }.also { job ->
                if (autoClose) {
                    job.join()
                    closeChannel(id)
                }
            }.join()
        }
    }
}

@RabbitDslMarker
fun PluginContext.connectionTest(
    id: String,
    autoClose: Boolean = false,
    block: suspend ConnectionContext.() -> Unit
) = runBlocking {
    with(connectionManager) {
        getConnection(id).also {
            coroutineScope.launch(Dispatchers.rabbitMQ) {
                ConnectionContext(connectionManager, it).apply { block() }
            }.also { job ->
                if (autoClose) {
                    job.join()
                    closeConnection(id)
                }
            }.join()
        }
    }
}

@RabbitDslMarker
fun PluginContext.libConnectionTest(
    id: String,
    autoClose: Boolean = false,
    block: suspend Connection.() -> Unit
) = runBlocking {
    with(connectionManager) {
        getConnection(id).also {
            coroutineScope.launch(Dispatchers.rabbitMQ) {
                it.apply { block() }
            }.also { job ->
                if (autoClose) {
                    job.join()
                    closeConnection(id)
                }
            }.join()
        }
    }
}

@RabbitDslMarker
inline fun ConnectionContext.channelTest(
    id: Int,
    autoClose: Boolean = false,
    crossinline block: suspend ChannelContext.() -> Unit
) = runBlocking {
    with(connectionManager) {
        getChannel(id, getConnectionId(connection)).also {
            coroutineScope.launch(Dispatchers.rabbitMQ) {
                it.also { ChannelContext(connectionManager, it).apply { block() } }
            }.also { job ->
                if (autoClose) {
                    job.join()
                    closeChannel(id)
                }
            }.join()
        }
    }
}

@RabbitDslMarker
inline fun ConnectionContext.channelTest(
    autoClose: Boolean = false,
    crossinline block: suspend ChannelContext.() -> Unit
) = runBlocking {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = Random.nextInt(1000, 5000)
        connectionManager.getChannel(channelId, connectionId).also {
            coroutineScope.launch(Dispatchers.rabbitMQ) {
                it.also { ChannelContext(connectionManager, it).apply { block() } }
            }.also { job ->
                if (autoClose) {
                    job.join()
                    closeChannel(channelId)
                }
            }.join()
        }
    }
}

@RabbitDslMarker
inline fun ConnectionContext.libChannel(
    autoClose: Boolean = false,
    crossinline block: suspend Channel.() -> Unit
) = runBlocking {
    with(connectionManager) {
        val connectionId = getConnectionId(connection)
        val channelId = Random.nextInt(1000, 5000)
        getChannel(channelId, connectionId).also {
            coroutineScope.launch(Dispatchers.rabbitMQ) {
                it.apply { block() }
            }.also { job ->
                if (autoClose) {
                    job.join()
                    closeChannel(channelId)
                }
            }.join()
        }
    }
}