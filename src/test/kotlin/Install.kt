import com.mesh.kabbitMq.KabbitMQ
import com.mesh.kabbitMq.dsl.extensions.*
import com.rabbitmq.client.BuiltinExchangeType
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import java.lang.Thread.sleep
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Serializable
data class Message(
    var content: String
)

@Serializable
data class Envelope(
    var id: String,
    var message: Message
)

class PluginTesting {

    @BeforeTest
    fun config() = testApplication {
        environment {
            config = ApplicationConfig("application.conf")
        }
    }

    @Test
    fun testInstall() = testApplication {
        application {
            install(KabbitMQ)

            // declare dead letter queue
            queueBind {
                queue = "dlq"
                exchange = "dlx"
                routingKey = "dlq-dlx"
                queueDeclare {
                    queue = "dlq"
                    durable = true
                }
                exchangeDeclare {
                    exchange = "dlx"
                    type = BuiltinExchangeType.DIRECT
                }
            }

            // declare queue configured with dead letter queue
            queueBind {
                queue = "test-queue"
                exchange = "test-exchange"
                queueDeclare {
                    queue = "test-queue"
                    arguments = mapOf(
                        "x-dead-letter-exchange" to "dlx",
                        "x-dead-letter-routing-key" to "dlq-dlx"
                    )
                }
                exchangeDeclare {
                    exchange = "test-exchange"
                    type = BuiltinExchangeType.FANOUT
                }
            }

            repeat(10) {
                basicPublish {
                    exchange = "test-exchange"
                    message {
                        Message(content = "Hello world!")
                    }
                }
            }

            val deliveredMessages = mutableListOf<Message>()
            val dlqMessages = mutableListOf<Message>()

            basicConsume {
                queue = "test-queue"
                autoAck = false
                deliverCallback<Message> { tag, message ->
                    deliveredMessages.add(message)
                    basicReject {
                        deliveryTag = tag
                        requeue = false
                    }
                }
            }

            assertEquals(1, consumerCount { queue = "test-queue" })

            basicConsume {
                queue = "dlq"
                autoAck = true
                deliverCallback<Message> { _, message ->
                    dlqMessages.add(message)
                    println("Message in DLQ: $message")
                }
            }

            sleep(1000)

            assertEquals(10, deliveredMessages.size, "Expected 10 messages to be delivered to the test-queue.")
            assertEquals(10, dlqMessages.size, "Expected 10 messages to be in the DLQ after being rejected.")

            dlqMessages.forEach { message ->
                assertTrue(message.content.contains("Hello world!"), "Message in DLQ should contain 'Hello world!'")
            }
        }
    }
}
