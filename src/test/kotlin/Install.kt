import com.mesh.kabbitMq.KabbitMQ
import com.mesh.kabbitMq.dsl.*
import com.rabbitmq.client.BuiltinExchangeType
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlin.test.Test

private fun Application.installModule() {
    install(KabbitMQ) {
        uri = "amqp://guest:guest@localhost:5672"
        connectionName = "localhost"
    }
}

@Serializable
data class Message(
    var text: String = "fdsfsd",
    var mama: String = "aaaaa"
)

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
                        message{
                            Message("Test", "Test")
                        }
                    }
                }

                messageCount {
                    queue = "test_queue"
                }.let(::println)

                basicConsume {
                    queue = "test_queue"
                    deliverCallback<Message>{ tag, message ->
                        println("$tag: $message")
                    }
                    cancelCallback { tag ->
                       println(tag)
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
                        message = "".toByteArray()
                    }
                }

                channel("test") {
                    basicConsume {
                        consumerTag = "cancel"
                        queue = "test_queue"
                        deliverCallback<Message> { _, message ->
                            println("Consummmerrrrrrrr")
                            message.let(::println)
                        }
                        cancelCallback { _ ->
                            println("cancelled")
                        }
                    }

                    basicCancel("cancel")
                    close()
                }


                channel("test1") {
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

