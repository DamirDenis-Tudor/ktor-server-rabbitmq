package integration

import channelTest
import connectionTest
import io.github.damir.denis.tudor.ktor.server.rabbitmq.RabbitMQ
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.utility.DockerImageName
import rabbitmqTest
import kotlin.test.assertNotEquals

class ConnectionTests {

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
    fun `test install with default channel`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }

            rabbitmqTest {
                assert(channel.isOpen)
            }
        }
    }

    @Test
    fun `test connection reuse`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }

        application {
            runTest {
                rabbitmqTest {
                    val connection1 = connectionTest(id = "connection_1") {}
                    val connection1Reused = connectionTest(id = "connection_1") {}

                    Assertions.assertEquals(connection1Reused, connection1)
                }
            }
        }
    }

    @Test
    fun `test connection channel reuse`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }

        application {
            runTest {
                rabbitmqTest {
                    val channel = channelTest(id = 99) {}
                    val channelReused = channelTest(id = 99) {}

                    Assertions.assertEquals(channel, channelReused)
                }
            }
        }
    }

    @Test
    fun `test channel reuse within a connection block`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }

        application {
            runTest {
                rabbitmqTest {
                    connectionTest(id = "test") {
                        val channelDefault = channelTest {}

                        Assertions.assertEquals(channel, channelDefault)
                    }
                }
            }
        }
    }

    @Test
    fun `test autoclose channel`() = testApplication {

        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }
        application {
            runTest {
                rabbitmqTest {
                    val channel1 = channelTest(id = 99, autoClose = true) {}
                    val channel2 = channelTest(id = 99) {}

                    assertNotEquals(channel1, channel2)
                }
            }
        }
    }

    @Test
    fun `test autoclose connection channel`() = testApplication {
        application {
            install(RabbitMQ) {
                connectionAttempts = 3
                attemptDelay = 10
                uri = rabbitMQContainer.amqpUrl
            }
        }
        application {
            runTest {
                rabbitmqTest {
                    connectionTest(id = "test") {
                        val channel1 = channelTest(id = 99, autoClose = true) {}
                        val channel2 = channelTest(id = 99) {}
                        assertNotEquals(channel1, channel2)
                    }
                }
            }
        }
    }
}
