plugins {
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.jreleaser)
    id("maven-publish")
    id("signing")
}

allprojects {
    group = "io.github.damirdenis-tudor"
    version = project.findProperty("releaseVersion") ?: "0.0.1"

    val mavenCentralUsername = project.findProperty("mavenCentralUsername")?.toString() ?: ""
    val mavenCentralPasswordToken = project.findProperty("mavenCentralPasswordToken")?.toString() ?: ""
    val makeDeployment = project.findProperty("makeDeployment")?.toString()?.toBoolean() != false
    val githubToken = project.findProperty("githubToken")?.toString() ?: "no_blank"

    repositories {
        mavenCentral()
    }

    /*
    publishing {
        publications {
            create<MavenPublication>("kotlin") {
                groupId = "io.github.damirdenis-tudor"
                artifactId = "ktor-server-rabbitmq"
                //from(components["java"])

                //artifact(tasks["kotlinSourcesJar"])
                //artifact(tasks["javadocJar"])

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
//        mavenLocal()
        }
    }

    signing {
        sign(publishing.publications["kotlin"])
    }

    jreleaser {
        release {
            github {
                token = githubToken

                changelog {

                    enabled = true
                    setFormatted("ALWAYS")
                    preset = "conventional-commits"
                    extraProperties = mapOf("categorizeScopes" to true)
                    contributors {
                        enabled = false
                    }
                    append {
                        setTarget("build/jreleaser/release/CHANGELOG.md")
                        enabled = true
                        content = """## Maven artifacts
                         - [ktor-server-rabbitmq:$version](https://central.sonatype.com/artifact/io.github.damirdenis-tudor/ktor-server-rabbitmq/$version)
                         """
                    }
                }
            }
            project {
                name = "ktor-server-rabbitmq"
                description.set("Ktor Server RabbitMQ plugin")
                copyright.set("Damir Denis-Tudor")
            }
            if (makeDeployment) {
                deploy {
                    maven {
                        mavenCentral {
                            create("sonatype") {
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
    }
    */
}
