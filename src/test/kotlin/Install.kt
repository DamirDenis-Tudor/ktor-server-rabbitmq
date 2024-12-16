import io.github.damir.denis.tudor.ktor.server.rabbitmq.KabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.extensions.*
import com.rabbitmq.client.BuiltinExchangeType
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.ktor.util.logging.*
import kotlinx.serialization.Serializable
import kotlin.test.BeforeTest
import kotlin.test.Test

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
    fun connectionTesting() = testApplication {
        application {
            install(KabbitMQ) {
                uri = "amqp://guest:guest@localhost:5672"
                connectionAttempts = 1
                tlsEnabled = true
            }
        }


        application {
            /* default connection, default channel */
            messageCount { queue = "test-queue" }

            channel(id = 2, autoClose = false) {
                /* calls */
            }

            /* new connection */
            connection(id = "connection_0", autoClose = false) {
                /* new channel */
                channel(id = 2, autoClose = false) {
                    /* calls */
                }

                /* will be destroyed after the task is finished */
                channel {
                    /* calls */
                }
            }

            /* reused connection */
            connection(id = "connection_0") {
                /* reused channel */
                channel(id = 2, autoClose = false) {
                    /* calls */
                }
                /* new channel */
                channel(id = 3, autoClose = false) {
                    /* calls */
                }
            }

            /* default connection, new channel */
            channel(id = 4, autoClose = false) {
                /* calls */
            }

            while (true) {
            }
        }
    }

    @Test
    fun wrong() = testApplication {
        application {
            install(KabbitMQ) {
                uri = "amqp://guest:guest@localhost:5672"
            }
        }
        application {
            basicConsume {
                queue = "test-queue"
                /* autoAck = true */ // let's say that autoAck is omited
                deliverCallback<Message> { tag, message ->
                    println("Message: $message with $tag")
                }
            }
        }
    }

    @Test
    fun testInstall() = testApplication {
        application {
            install(KabbitMQ) {
                uri = "amqp://guest:guest@localhost:5672"
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
            channel(55, autoClose = true) {
                queueBind {
                    queue = "test-queue111"
                    exchange = "test-exchange111"
                    queueDeclare {
                        queue = "test-queue111"
                    }
                    exchangeDeclare {
                        exchange = "test-exchange111"
                        type = BuiltinExchangeType.FANOUT
                    }
                }
            }

            channel(55, autoClose = false) {
                repeat(10) {
                    basicPublish {
                        exchange = "test-exchange"
                        message {
                            Message(content = "Hello world!")
                        }
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
            channel(1, autoClose = true) {}

            basicConsume {
                queue = "test-queue"
                autoAck = false
                deliverCallback<Message> { tag, message ->
                    basicReject {
                        deliveryTag = tag
                        requeue = false
                    }
                }
            }

            val logger = KtorSimpleLogger("test")
            connection("intensive") {
                channel(4) {
                    basicConsume {
                        queue = "dlq"
                        autoAck = true
                        deliverCallback<Message> { _, message ->
                            logger.info("Message in DLQ: $message")
                        }
                    }
                }
            }


            while (true) {
            }
        }
    }
}
