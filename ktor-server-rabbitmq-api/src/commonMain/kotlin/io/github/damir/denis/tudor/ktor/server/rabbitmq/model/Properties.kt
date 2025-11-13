package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class Properties(
    val contentType: String? = null,
    val contentEncoding: String? = null,
    val headers: Map<String, Any?>? = null,
    val deliveryMode: Int? = null,
    val priority: Int? = null,
    val correlationId: String? = null,
    val replyTo: String? = null,
    val expiration: String? = null,
    val messageId: String? = null,
    val timestamp: Long? = null,
    val type: String? = null,
    val userId: String? = null,
    val appId: String? = null,
    val clusterId: String? = null,
)
