package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl

import com.rabbitmq.client.Channel
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.BasicAckBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.BasicConsumeBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.BasicGetBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.BasicNackBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.BasicPublishBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.BasicQosBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.BasicRejectBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.ConsumerCountBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.ExchangeDeclareBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.ExchangeDeleteBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.MessageCountBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.QueueBindBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.QueueDeclareBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.QueueDeleteBuilder
import io.github.damir.denis.tudor.ktor.server.rabbitmq.builders.QueueUnbindBuilder

open class ChannelContext(val channel: Channel)

@RabbitDslMarker
fun ChannelContext.basicAck(block: BasicAckBuilder.() -> Unit) = BasicAckBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.basicConsume(block: BasicConsumeBuilder.() -> Unit) = BasicConsumeBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.basicGet(block: BasicGetBuilder.() -> Unit) = BasicGetBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.basicNack(block: BasicNackBuilder.() -> Unit) = BasicNackBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.basicPublish(block: BasicPublishBuilder.() -> Unit) = BasicPublishBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.basicQos(block: BasicQosBuilder.() -> Unit) = BasicQosBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.basicReject(block: BasicRejectBuilder.() -> Unit) = BasicRejectBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.consumerCount(block: ConsumerCountBuilder.() -> Unit) = ConsumerCountBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.exchangeDeclare(block: ExchangeDeclareBuilder.() -> Unit) = ExchangeDeclareBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.exchangeDelete(block: ExchangeDeleteBuilder.() -> Unit) = ExchangeDeleteBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.messageCount(block: MessageCountBuilder.() -> Unit) = MessageCountBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.queueBind(block: QueueBindBuilder.() -> Unit) = QueueBindBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.queueDeclare(block: QueueDeclareBuilder.() -> Unit) = QueueDeclareBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.queueDelete(block: QueueDeleteBuilder.() -> Unit) = QueueDeleteBuilder(channel).apply(block).build()

@RabbitDslMarker
fun ChannelContext.queueUnbind(block: QueueUnbindBuilder.() -> Unit) = QueueUnbindBuilder(channel).apply(block).build()