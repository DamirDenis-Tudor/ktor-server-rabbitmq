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
    fun wrong() = testApplication {
        application {
            install(KabbitMQ) {
                uri = "amqp://guest:guest@localhost:5672"
                connectionName = "guest"
            }

            channel("expensive", autoClose = true) {
                queueBind {
                    queueDeclare {
                        queue = "test"
                        durable = true
                    }
                    exchangeDeclare {
                        exchange = "test-x"
                        type = BuiltinExchangeType.DIRECT
                    }
                    exchange = "test-x"
                    queue = "test"
                    routingKey = "test-routing"
                }

                basicPublish {
                    exchange = "test-x"
                    routingKey = "test-routing"
                    message {
                        Envelope("", Message("fds"))
                    }
                }

                basicConsume {
                    queue = "test"
                    autoAck = true
                    deliverCallback<Envelope> { tag, message ->
                        println(message)
                    }
                    cancelCallback {  }
                }
            }



            connection("expensive", autoClose = false){
                channel {
                    println()
                }
            }


        }
    }

    @Test
    fun testInstall() = testApplication {
        application {
            install(KabbitMQ) {
                uri = "amqp://guest:guest@localhost:5672"
                connectionName = "guest"
            }

            channel("direct-calls"){
                basicPublish("test", "test-routing-key", null, "fdsf".toByteArray())
            }

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
            /* channels will be terminated after task completion */
            connection("intensive", autoClose = false) {
                channel {
                    messageCount { queue = "test-queue" }
                }
                channel { }
            }
            channel("intensive", autoClose = true) {}


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
