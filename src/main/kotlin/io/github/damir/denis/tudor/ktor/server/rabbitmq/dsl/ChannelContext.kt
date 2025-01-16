package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.*
import io.github.damir.denis.tudor.ktor.server.rabbitmq.connection.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class ChannelContext(
    open val connectionManager: ConnectionManager,
    val channel: Channel
)

@RabbitDslMarker
suspend fun ChannelContext.basicAck(block: suspend BasicAckBuilder.() -> Unit) = runCatching {
    withContext(Dispatchers.IO) { BasicAckBuilder(channel).apply { this.block() }.build() }
}

@RabbitDslMarker
suspend fun ChannelContext.basicConsume(block: suspend BasicConsumeBuilder.() -> Unit) = runCatching {
    with(connectionManager) {
        withContext(dispatcher) {
            BasicConsumeBuilder(this@with, channel).apply { this.block() }.build()
        }
    }
}

@RabbitDslMarker
suspend fun ChannelContext.basicGet(block: suspend BasicGetBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        BasicGetBuilder(channel).apply { this.block() }.build()
    }
}

@RabbitDslMarker
suspend fun ChannelContext.basicNack(block: suspend BasicNackBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        BasicNackBuilder(channel).apply { this.block() }.build()
    }
}

@RabbitDslMarker
suspend fun ChannelContext.basicPublish(block: suspend BasicPublishBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        BasicPublishBuilder(channel).apply { this.block() }.build()
    }
}

@RabbitDslMarker
suspend fun ChannelContext.basicQos(block: suspend BasicQosBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        BasicQosBuilder(channel).apply { this.block() }.build()
    }
}

@RabbitDslMarker
suspend fun ChannelContext.basicReject(block: suspend BasicRejectBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        BasicRejectBuilder(channel).apply { this.block() }.build()
    }
}

@RabbitDslMarker
suspend fun ChannelContext.consumerCount(block: suspend ConsumerCountBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        ConsumerCountBuilder(channel).apply { this.block() }.build()
    }
}

@RabbitDslMarker
suspend fun ChannelContext.exchangeDeclare(block: suspend ExchangeDeclareBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        ExchangeDeclareBuilder(channel).apply { this.block() }.build()
    }
}

@RabbitDslMarker
suspend fun ChannelContext.exchangeDelete(block: suspend ExchangeDeleteBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        ExchangeDeleteBuilder(channel).apply { this.block() }.build()
    }
}

@RabbitDslMarker
suspend fun ChannelContext.messageCount(block: suspend MessageCountBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        MessageCountBuilder(channel).apply { this.block() }.build()
    }
}

@RabbitDslMarker
suspend fun ChannelContext.queueBind(block: suspend QueueBindBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        QueueBindBuilder(channel).apply { this.block() }.build()
    }
}

@RabbitDslMarker
suspend fun ChannelContext.queueDeclare(block: suspend QueueDeclareBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        QueueDeclareBuilder(channel).apply { this.block() }.build()
    }
}

@RabbitDslMarker
suspend fun ChannelContext.queueDelete(block: suspend QueueDeleteBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        QueueDeleteBuilder(channel).apply { this.block() }.build()
    }
}

@RabbitDslMarker
suspend fun ChannelContext.queueUnbind(block: suspend QueueUnbindBuilder.() -> Unit) = runCatching {
    withContext(connectionManager.dispatcher) {
        QueueUnbindBuilder(channel).apply { this.block() }.build()
    }
}