package io.github.damir.denis.tudor.ktor.server.rabbitmq.model

data class GetResponse(
    val envelope: Envelope,
    val props: Properties,
    val body: ByteArray,
    val messageCount: Int,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GetResponse

        if (messageCount != other.messageCount) return false
        if (envelope != other.envelope) return false
        if (props != other.props) return false
        if (!body.contentEquals(other.body)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = messageCount
        result = 31 * result + envelope.hashCode()
        result = 31 * result + props.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }

}
