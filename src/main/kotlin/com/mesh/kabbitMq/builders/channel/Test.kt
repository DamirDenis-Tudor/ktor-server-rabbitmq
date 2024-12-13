package com.mesh.kabbitMq.builders.channel

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel

// Base DSL class for shared configuration
abstract class PublishDSL(protected val channel: Channel) {
    var exchange: String = ""
    var routingKey: String = ""
    var props: BasicProperties? = null
    var body: ByteArray = ByteArray(0)

    fun properties(block: BasicProperties.Builder.() -> Unit) {
        props = BasicProperties.Builder().apply(block).build()
    }

    abstract fun publish()
}

class PublishWithoutFlagsDSL(channel: Channel) : PublishDSL(channel) {
    override fun publish() {
        channel.basicPublish(exchange, routingKey, props, body)
    }
}

class PublishWithMandatoryDSL(channel: Channel) : PublishDSL(channel) {
    var mandatory: Boolean = false

    override fun publish() {
        channel.basicPublish(exchange, routingKey, mandatory, props, body)
    }
}

class PublishWithMandatoryAndImmediateDSL(channel: Channel) : PublishDSL(channel) {
    var mandatory: Boolean = false
    var immediate: Boolean = false

    override fun publish() {
        channel.basicPublish(exchange, routingKey, mandatory, immediate, props, body)
    }
}

