import com.mesh.kabbitMq.KabbitMQ
import com.mesh.kabbitMq.dsl.*
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.DeliverCallback
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import java.nio.charset.Charset
import kotlin.test.Test

private fun Application.installModule() {
    install(KabbitMQ) {
        uri = "amqp://guest:guest@localhost:5672"
        connectionName = "localhost"
    }
}

class PluginTesting {

    @Test
    fun testInstall() = runBlocking {
        runTestApplication {
            application {
                installModule()

                repeat(10) {
                    basicPublish {
                        exchange = "test_exchange"
                        routingKey = "test_routing_key"
                        message = "test"
                        basicProperties {

                        }
                    }
                }

                messageCount {
                    queue = "test_queue"
                }.let(::println)

                basicConsume {
                    queue = "test_queue"
                    deliverCallback = DeliverCallback { _, message ->
                        message.body.toString(Charset.defaultCharset()).let(::println)
                    }
                }
            }
        }
    }

    @Test
    fun testInstall1() = runBlocking {
        runTestApplication {
            application {
                installModule()

                repeat(10) {
                    basicPublish {
                        exchange = "test_exchange"
                        routingKey = "test_routing_key"
                        message = "test"
                    }
                }

                channel("test") {
                    basicConsume {
                        consumerTag = "cancel"
                        queue = "test_queue"
                        deliverCallback = DeliverCallback { _, message ->
                            println("Consummmerrrrrrrr")
                            message.body.toString(Charset.defaultCharset()).let(::println)
                        }
                        cancelCallback = CancelCallback { _ ->
                            println("cancelled")
                        }
                    }

                    basicCancel("cancel")
                    close()
                }


                channel("test1") {
                    basicConsume {
                        queue = "test_queue"
                        deliverCallback = DeliverCallback { _, message ->
                            message.body.toString(Charset.defaultCharset()).let(::println)
                        }
                        cancelCallback = CancelCallback { _ ->
                            println("cancelled")
                        }
                    }
                }


                exchangeDeclare {
                    exchange = "dead-letter-exchange"
                    type = BuiltinExchangeType.DIRECT
                    durable = true
                    autoDelete = true
                    arguments = mapOf()
                }

                queueDeclare {
                    queue = "dead-letter-queue"
                    durable = true
                    arguments = mapOf()
                }

                queueBind {
                    queue = "dead-letter-queue"
                    routingKey = "routing-key"
                    exchange = "dead-letter-exchange"
                }

                while (true) {
                }
            }
        }
    }
}

