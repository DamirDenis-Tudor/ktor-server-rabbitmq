package com.mesh.kabbitMq.builders


import com.rabbitmq.client.Channel

// Builder for basicQos


// Builder for exchangeDeclare


// Builder for basicPublish


// Builder for queueBind


// Builder for confirmSelect
class KabbitMQConfirmSelectBuilder(private val channel: Channel) {
    fun build() {
        channel.confirmSelect()
    }
}

// Builder for txSelect
class KabbitMQTxSelectBuilder(private val channel: Channel) {
    fun build() {
        channel.txSelect()
    }
}

// Builder for txCommit
class KabbitMQTxCommitBuilder(private val channel: Channel) {
    fun build() {
        channel.txCommit()
    }
}

// Builder for txRollback
class KabbitMQTxRollbackBuilder(private val channel: Channel) {
    fun build() {
        channel.txRollback()
    }
}

