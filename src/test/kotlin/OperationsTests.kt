import io.github.damir.denis.tudor.ktor.server.rabbitmq.plugin.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.github.damir.denis.tudor.ktor.server.rabbitmq.plugin.rabbitmq
import kotlinx.serialization.Serializable
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.DockerImageName
import kotlin.test.assertEquals

class OperationsTests {

    @Serializable
    data class Message(
        var content: String
    )

    companion object {
        private val rabbitMQContainer: RabbitMQContainer = RabbitMQContainer(
            DockerImageName.parse("rabbitmq:management")
        )

        @BeforeAll
        @JvmStatic
        fun setUp() {
            rabbitMQContainer.start()
            println("RabbitMQ is running at ${rabbitMQContainer.amqpUrl}")
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            rabbitMQContainer.stop()
            println("RabbitMQ is stopped")
        }
    }

    @Test
    fun `queue create test`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }

        application {
            rabbitmq {
                runTest {
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
                            type = "direct"
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `dead letter queue test`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }

        application {
            rabbitmq {
                runTest {
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
                            type = "direct"
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
                            type = "fanout"
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

                    assertEquals(messageCount { queue = "test-queue" }, 10)

                    basicConsume {
                        queue = "test-queue"
                        autoAck = false
                        deliverCallback<Message> { tag, message ->
                            /* process message */
                            // ...

                            /* simulate something went wrong */
                            basicReject {
                                deliveryTag = tag
                                requeue = false
                            }
                        }
                    }

                    Thread.sleep(2_000)

                    assertEquals(messageCount { queue = "dlq" }, 10)

                    basicConsume {
                        queue = "dlq"
                        autoAck = true
                        deliverCallback<Message> { tag, message ->
                            println("Received message: $message")
                        }
                    }

                    Thread.sleep(2_000)

                    assertEquals(messageCount { queue = "dlq" }, 0)
                }
            }
        }
    }
}