package com.mesh.kabbitMq.dsl

import com.mesh.kabbitMq.builders.channel.KabbitMQBasicConsumeBuilder
import com.mesh.kabbitMq.builders.channel.*
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection

@KabbitMQDslMarker
inline fun Connection.channel(id: String, block: Channel.() -> Unit): Channel =
    this.createChannel().also(block)

@KabbitMQDslMarker
inline fun Channel.basicConsume(block: KabbitMQBasicConsumeBuilder.() -> Unit) =
    KabbitMQBasicConsumeBuilder(this).apply(block).build()


fun Channel.publish1(block: PublishWithoutFlagsDSL.() -> Unit) {
    PublishWithoutFlagsDSL(this).apply(block).publish()
}

fun Channel.publish2(block: PublishWithMandatoryDSL.() -> Unit) {
    PublishWithMandatoryDSL(this).apply(block).publish()
}

fun Channel.publish3(block: PublishWithMandatoryAndImmediateDSL.() -> Unit) {
    PublishWithMandatoryAndImmediateDSL(this).apply(block).publish()
}

