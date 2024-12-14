package com.mesh.kabbitMq.service

import com.mesh.kabbitMq.dsl.KabbitMQDslMarker
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.annotations.ApiStatus.Internal

class KabbitMQConfig {
    lateinit var uri: String
    lateinit var connectionName: String


}