import com.mesh.kabbitMq.KabbitMQ
import com.mesh.kabbitMq.builders.channel.PublishWithoutFlagsDSL
import com.mesh.kabbitMq.dsl.*
import com.rabbitmq.client.AMQP
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

                basicPublish {
                    exchange = ""
                    routingKey = ""
                    message = ""
                    basicProperties = AMQP.BasicProperties().apply {
                        headers["Content-Type"] = "application/json"
                    }
                }
            }
        }

        @Test
        fun testInstall() = runBlocking {
            runTestApplication {
                application {
                    installModule()

                    connection("connection-1") {
                        channel("consume-channel-1") {
                            basicConsume {
                                queue = "test_queue"
                                deliverCallback = DeliverCallback { _, message ->
                                    message.body.toString(Charset.defaultCharset()).let(::println)
                                }
                                cancelCallback = CancelCallback { _ ->
                                    println("cancelled")
                                }
                            }

                            basicPublish {
                                exchange = ""
                            }
                        }
                    }

                    channel("consume-channel-2") {
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
}
