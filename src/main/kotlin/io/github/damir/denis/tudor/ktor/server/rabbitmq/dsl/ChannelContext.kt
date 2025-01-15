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
suspend fun ChannelContext.basicAck(block: suspend BasicAckBuilder.() -> Unit) =
    withContext(Dispatchers.IO) { BasicAckBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.basicConsume(block: suspend BasicConsumeBuilder.() -> Unit) {
    with(connectionManager) {
        withContext(dispatcher) {
            coroutineScope.launch(dispatcher) {
                BasicConsumeBuilder(this@with, channel).apply { this.block() }.build()
            }
        }
    }
}

@RabbitDslMarker
suspend fun ChannelContext.basicGet(block: suspend BasicGetBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { BasicGetBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.basicNack(block: suspend BasicNackBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { BasicNackBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.basicPublish(block: suspend BasicPublishBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { BasicPublishBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.basicQos(block: suspend BasicQosBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { BasicQosBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.basicReject(block: suspend BasicRejectBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { BasicRejectBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.consumerCount(block: suspend ConsumerCountBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { ConsumerCountBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.exchangeDeclare(block: suspend ExchangeDeclareBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { ExchangeDeclareBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.exchangeDelete(block: suspend ExchangeDeleteBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { ExchangeDeleteBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.messageCount(block: suspend MessageCountBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { MessageCountBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.queueBind(block: suspend QueueBindBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { QueueBindBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.queueDeclare(block: suspend QueueDeclareBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { QueueDeclareBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.queueDelete(block: suspend QueueDeleteBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { QueueDeleteBuilder(channel).apply { this.block() }.build() }

@RabbitDslMarker
suspend fun ChannelContext.queueUnbind(block: suspend QueueUnbindBuilder.() -> Unit) =
    withContext(connectionManager.dispatcher) { QueueUnbindBuilder(channel).apply { this.block() }.build() }
