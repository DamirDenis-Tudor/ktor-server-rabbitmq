import org.jreleaser.model.Active

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val rabbitmqVersion: String by project
val kotlinxVersion: String by project

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("org.jreleaser") version "1.15.0"
    id("maven-publish")
    id("signing")
}

group = "io.github.damirdenis-tudor"
version = project.findProperty("releaseVersion") ?: "1.0.0"

val mavenCentralUsername = project.findProperty("mavenCentralUsername")?.toString() ?: ""
val mavenCentralPasswordToken = project.findProperty("mavenCentralPasswordToken")?.toString() ?: ""
val githubToken = project.findProperty("githubToken")?.toString() ?: "no_blank"

repositories {
    mavenCentral()
}

dependencies {
    // ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")

    //serializing
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxVersion")

    // rabbitmq
    api("com.rabbitmq:amqp-client:$rabbitmqVersion")

    // logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // testing
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks["javadoc"])
}

publishing {
    publications {
        create<MavenPublication>("kotlin") {
            groupId = "io.github.damirdenis-tudor"
            artifactId = "ktor-server-rabbitmq"
            from(components["java"])

            artifact(tasks["kotlinSourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("Ktor rabbitMQ plugin")
                packaging = "jar"
                description.set(
                    "KabbitMQ is a Ktor plugin for RabbitMQ that provides access to all the core functionalities of the com.rabbitmq:amqp-client.\n" +
                            "It integrates seamlessly with Ktor's DSL, offering readable, maintainable, and easy-to-use functionalities.\n"
                )

                url.set("https://github.com/DamirDenis-Tudor/ktor-server-rabbitmq")

                scm {
                    connection.set("scm:git:https://github.com/DamirDenis-Tudor/ktor-server-rabbitmq.git")
                    developerConnection.set("scm:git:git@github.com:DamirDenis-Tudor/ktor-server-rabbitmq.git")
                    url.set("https://github.com/DamirDenis-Tudor/ktor-server-rabbitmq")
                }

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("DamirDenis-Tudor")
                        name.set("Damir Denis-Tudor")
                        email.set("denis-tudor.damir@student.tuiasi.ro")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        }
    }
}

signing {
    sign(publishing.publications["kotlin"])
}

jreleaser {
    release {
        github{
            token = githubToken
        }
        project {
            name = "ktor-server-rabbitmq"
            description.set("Ktor Server RabbitMQ plugin")
            copyright.set("Damir Denis-Tudor")
        }
        deploy {
            maven {
                mavenCentral {
                    create("sonatype"){
                        active = Active.RELEASE
                        url = "https://central.sonatype.com/api/v1/publisher"

                        snapshotSupported = true

                        setAuthorization("BEARER")
                        username = mavenCentralUsername
                        password = mavenCentralPasswordToken

                        stagingRepository("build/staging-deploy")

                        connectTimeout = 20
                        readTimeout = 60
                        sign = false

                        verifyUrl = "https://repo1.maven.org/maven2/{{path}}/{{filename}}"
                        namespace = "io.github.damirdenis-tudor"

                        retryDelay = 60
                        maxRetries = 100
                    }
                }
            }
        }
    }
}