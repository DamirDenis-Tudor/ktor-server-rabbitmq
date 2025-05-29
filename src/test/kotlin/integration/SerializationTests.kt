package integration

import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.DockerImageName
import rabbitmqTest
import java.lang.Thread.sleep
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals

class SerializationTests {

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
    fun `every message should be able to be serialized as String`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
                consumerChannelCoroutineSize = 100
            }
        }

        application {
            rabbitmqTest {
                queueBind {
                    queue = "demo1-queue"
                    exchange = "demo1-exchange"
                    routingKey = "demo1-routing-key"
                    queueDeclare {
                        queue = "demo1-queue"
                    }
                    exchangeDeclare {
                        exchange = "demo1-exchange"
                        type = "direct"
                    }
                }
            }

            rabbitmqTest {
                repeat(10) {
                    basicPublish {
                        exchange = "demo1-exchange"
                        routingKey = "demo1-routing-key"
                        message { Message(content = "Hello World, 'Message'!") }
                    }
                }
                repeat(90) {
                    basicPublish {
                        exchange = "demo1-exchange"
                        routingKey = "demo1-routing-key"
                        message { "Hello World, 'String'!" }
                    }
                }
            }

            val counter1 = AtomicInteger(0)

            rabbitmqTest {
                basicConsume {
                    autoAck = true
                    queue = "demo1-queue"
                    dispatcher = Dispatchers.IO
                    deliverCallback<String> { tag, message ->
                        delay(3)
                        counter1.incrementAndGet()
                        log.info("Consume1 : $message")
                    }
                }
            }

            sleep(2_000)

            log.info(counter1.toString())

            assert(counter1.get() == 100)
        }
    }

    @Test
    fun `every message should be able to be serialized as ByteArray`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
                consumerChannelCoroutineSize = 100
            }
        }

        application {
            rabbitmqTest {
                queueBind {
                    queue = "demo1-queue"
                    exchange = "demo1-exchange"
                    routingKey = "demo1-routing-key"
                    queueDeclare {
                        queue = "demo1-queue"
                    }
                    exchangeDeclare {
                        exchange = "demo1-exchange"
                        type = "direct"
                    }
                }
            }

            rabbitmqTest {
                repeat(10) {
                    basicPublish {
                        exchange = "demo1-exchange"
                        routingKey = "demo1-routing-key"
                        message { Message(content = "Hello World, 'Message'!") }
                    }
                }
                repeat(90) {
                    basicPublish {
                        exchange = "demo1-exchange"
                        routingKey = "demo1-routing-key"
                        message { "Hello World, 'String'!" }
                    }
                }
            }

            val counter1 = AtomicInteger(0)

            rabbitmqTest {
                basicConsume {
                    autoAck = true
                    queue = "demo1-queue"
                    dispatcher = Dispatchers.IO
                    deliverCallback<ByteArray> { tag, message ->
                        delay(3)
                        counter1.incrementAndGet()
                        log.info("Consume1 : ${message.contentToString()}")
                    }
                }
            }

            sleep(2_000)

            log.info(counter1.toString())

            assert(counter1.get() == 100)
        }
    }

    @Test
    fun `when a message cannot be serialized ignore messages`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
                consumerChannelCoroutineSize = 100
            }
        }

        application {
            rabbitmqTest {
                queueBind {
                    queue = "demo1-queue"
                    exchange = "demo1-exchange"
                    routingKey = "demo1-routing-key"
                    queueDeclare {
                        queue = "demo1-queue"
                    }
                    exchangeDeclare {
                        exchange = "demo1-exchange"
                        type = "direct"
                    }
                }
            }

            rabbitmqTest {
                repeat(10) {
                    basicPublish {
                        exchange = "demo1-exchange"
                        routingKey = "demo1-routing-key"
                        message { Message(content = "Hello World, 'Message'!") }
                    }
                }
                repeat(90) {
                    basicPublish {
                        exchange = "demo1-exchange"
                        routingKey = "demo1-routing-key"
                        message { "Hello World, 'String'!" }
                    }
                }
            }

            val counter1 = AtomicInteger(0)

            rabbitmqTest {
                basicConsume {
                    autoAck = true
                    queue = "demo1-queue"
                    dispatcher = Dispatchers.IO
                    deliverCallback<Message> { tag, message ->
                        delay(3)
                        counter1.incrementAndGet()
                        log.info("Consume1 : $message")
                    }
                }
            }

            sleep(2_000)

            log.info(counter1.toString())

            assert(counter1.get() == 10)
        }
    }

    @Test
    fun `when a message cannot be serialized use callback mechanism that reject the message in a dlq`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
                consumerChannelCoroutineSize = 100
            }
        }

        application {
            rabbitmqTest {
                queueBind {
                    queue = "dlq1"
                    exchange = "dlx1"
                    routingKey = "dlq-dlx1"
                    queueDeclare {
                        queue = "dlq1"
                        durable = true
                    }
                    exchangeDeclare {
                        exchange = "dlx1"
                        type = "direct"
                    }
                }

                queueBind {
                    queue = "test-queue12"
                    exchange = "test-exchange12"
                    queueDeclare {
                        queue = "test-queue12"
                        arguments = mapOf(
                            "x-dead-letter-exchange" to "dlx1",
                            "x-dead-letter-routing-key" to "dlq-dlx1"
                        )
                    }
                    exchangeDeclare {
                        exchange = "test-exchange12"
                        type = "fanout"
                    }
                }
            }

            rabbitmqTest {
                repeat(10) {
                    basicPublish {
                        exchange = "test-exchange12"
                        message { Message(content = "Hello World, 'Message'!") }
                    }
                }
                repeat(90) {
                    basicPublish {
                        exchange = "test-exchange12"
                        message { "Hello World, 'String'!" }
                    }
                }
            }

            val counter1 = AtomicInteger(0)

            rabbitmqTest {
                basicConsume {
                    autoAck = false
                    queue = "test-queue12"
                    dispatcher = Dispatchers.IO
                    deliverCallback<Message> { _, _ ->
                        delay(3)
                        counter1.incrementAndGet()
                    }
                    deliverFailureCallback { tag, _ ->
                        basicReject {
                            deliveryTag = tag
                            requeue = false
                        }
                    }
                }
            }

            sleep(2_000)

            assert(counter1.get() == 10)

            rabbitmqTest {
                assertEquals(messageCount { queue = "dlq1" }, 90)
            }
        }
    }

    @Test
    fun `consume using Message based serialization`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
                consumerChannelCoroutineSize = 100
            }
        }

        application {
            rabbitmqTest {
                queueBind {
                    queue = "demo1-queue"
                    exchange = "demo1-exchange"
                    routingKey = "demo1-routing-key"
                    queueDeclare {
                        queue = "demo1-queue"
                    }
                    exchangeDeclare {
                        exchange = "demo1-exchange"
                        type = "direct"
                    }
                }
            }

            rabbitmqTest {
                repeat(10) {
                    basicPublish {
                        exchange = "demo1-exchange"
                        routingKey = "demo1-routing-key"
                        message { Message(content = "Hello World, 'Message'!") }
                    }
                }
                repeat(90) {
                    basicPublish {
                        exchange = "demo1-exchange"
                        routingKey = "demo1-routing-key"
                        message { "Hello World, 'String'!" }
                    }
                }
            }

            val counter1 = AtomicInteger(0)

            rabbitmqTest {
                basicConsume {
                    autoAck = true
                    queue = "demo1-queue"
                    dispatcher = Dispatchers.IO
                    deliverCallback<Message> { message ->
                        delay(3)
                        counter1.incrementAndGet()
                        log.info("Consume1 : ${message.body}")
                    }
                    deliverFailureCallback { message ->
                        log.info("Consume1 : ${String(message.body)}")
                    }
                }
            }

            sleep(2_000)

            log.info(counter1.toString())

            assert(counter1.get() == 10)
        }
    }
}