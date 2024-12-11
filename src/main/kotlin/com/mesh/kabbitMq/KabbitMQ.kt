package com.mesh.kabbitMq

import com.mesh.kabbitMq.config.KabbitMQConfig
import com.mesh.kabbitMq.service.IKabbitMQService
import com.mesh.kabbitMq.service.KabbitMQService
import io.ktor.server.application.*
import io.ktor.util.*

class KabbitMQ {

    companion object Feature : BaseApplicationPlugin<Application, KabbitMQConfig, IKabbitMQService> {

        private val RabbitMQKey = AttributeKey<IKabbitMQService>("RabbitMQ")

        override val key: AttributeKey<IKabbitMQService>
            get() = RabbitMQKey

        override fun install(
            pipeline: Application,
            configure: KabbitMQConfig.() -> Unit
        ): IKabbitMQService = KabbitMQService((KabbitMQConfig().apply(configure)))
            .apply { pipeline.attributes.put(key, this) }
    }
}