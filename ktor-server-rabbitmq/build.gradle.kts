plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.maven)
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL, true)
    signAllPublications()
    pom {
        name.set("Ktor RabbitMQ plugin")
        description.set(
            "A Ktor plugin for RabbitMQ that provides access to all the core functionalities of the com.rabbitmq:amqp-client.\n" +
                    "It integrates seamlessly with Ktor's DSL, offering readable, maintainable, and easy-to-use functionalities.\n"
        )

        url.set(project.ext.get("url")?.toString())
        licenses {
            license {
                name.set(project.ext.get("license.name")?.toString())
                url.set(project.ext.get("license.url")?.toString())
            }
        }
        developers {
            developer {
                id.set(project.ext.get("developer.1.id")?.toString())
                name.set(project.ext.get("developer.1.name")?.toString())
                email.set(project.ext.get("developer.1.email")?.toString())
                url.set(project.ext.get("developer.1.url")?.toString())
            }
            developer {
                id.set(project.ext.get("developer.2.id")?.toString())
                name.set(project.ext.get("developer.2.name")?.toString())
                email.set(project.ext.get("developer.2.email")?.toString())
                url.set(project.ext.get("developer.2.url")?.toString())
            }
        }
        scm {
            url.set(project.ext.get("scm.url")?.toString())
        }
    }
}

kotlin {
    jvmToolchain(17)
    jvm()

    applyDefaultHierarchyTemplate()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":ktor-server-rabbitmq-java"))
            }
        }
        /*
        val nonJvmMain by creating {
            dependencies {
                api(project(":ktor-server-rabbitmq-kourier"))
            }
        }
        */
    }
}
