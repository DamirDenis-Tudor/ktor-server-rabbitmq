package com.mesh.kabbitMq.config

import com.rabbitmq.client.Channel

class KabbitMQConfig{

    lateinit var uri: String
    lateinit var connectionName: String

    private var initializeBlock: (Channel.() -> Unit)? = null

    fun initialize(block: Channel.() -> Unit) {
        initializeBlock = block
    }

    fun applyInitialization(channel: Channel) : KabbitMQConfig {
        initializeBlock?.invoke(channel)
        return this
    }
}
