package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

data class Delivery(
    val envelope: Envelope,
    val properties: Properties,
    val body: ByteArray,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Delivery

        if (envelope != other.envelope) return false
        if (properties != other.properties) return false
        if (!body.contentEquals(other.body)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = envelope.hashCode()
        result = 31 * result + properties.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }

}
