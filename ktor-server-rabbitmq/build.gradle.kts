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
    // Tiers are in accordance with <https://kotlinlang.org/docs/native-target-support.html>
    // Tier 1
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()

    // Tier 2
    linuxX64()
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()

    // Tier 3
    mingwX64()
    watchosDeviceArm64()

    // jvm & js
    jvmToolchain(17)
    jvm()

    applyDefaultHierarchyTemplate()
    sourceSets {
        val commonMain by getting {}
        val jvmMain by getting {
            dependencies {
                api(project(":ktor-server-rabbitmq-java"))
            }
        }
        val nonJvmMain by creating {
            dependsOn(commonMain)
            dependencies {
                api(project(":ktor-server-rabbitmq-kourier"))
            }
        }

        // Make all non-JVM targets depend on the `nonJvmMain` source set
        sourceSets
            .filter { it.name.endsWith("Main") && it.name != "commonMain" && it.name != "jvmMain" }
            .forEach { it.dependsOn(nonJvmMain) }
    }
}
