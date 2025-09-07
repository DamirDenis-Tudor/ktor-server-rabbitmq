plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.ksp)
    alias(libs.plugins.maven)
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL, true)
    signAllPublications()
    pom {
        name.set("Ktor RabbitMQ plugin Java Implementation")
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
    jvm {
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
                testLogging {
                    events("passed", "skipped", "failed")
                    showCauses = true
                    showStackTraces = true
                    showExceptions = true
                    info.events = debug.events
                }
            }
        }
    }

    applyDefaultHierarchyTemplate()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":ktor-server-rabbitmq-api"))
                api(libs.amqp.java)
                implementation(libs.logback.classic)
            }
        }
        val jvmTest by getting {
            kotlin.srcDir("${project(":ktor-server-rabbitmq-api").projectDir}/src/sharedTest/kotlin")
            resources.srcDir("${project(":ktor-server-rabbitmq-api").projectDir}/src/sharedTest/resources")
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.tests.mockk)
                implementation(libs.ktor.server.test.host)
                implementation(libs.tests.containers)
                implementation(libs.tests.containers.rabbitmq)
                implementation(libs.tests.junit.jupiter.api)
                implementation(libs.tests.junit.jupiter.engine)
            }
        }
    }
}

/*
tasks.jar {
    exclude("logback.xml")
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks["javadoc"])
}
*/
