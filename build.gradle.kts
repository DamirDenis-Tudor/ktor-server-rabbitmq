import java.io.ByteArrayOutputStream
import kotlin.system.exitProcess

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val rabbitmqVersion: String by project

plugins {
    id("application")
    kotlin("jvm") version "2.1.0"
    id("maven-publish")
    id("signing")
}

group = "io.github.damirdenis-tudor"
version = project.findProperty("releaseVersion") ?: ""

application {
    mainClass.set("com.mesh.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("com.rabbitmq:amqp-client:$rabbitmqVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
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
            artifactId = "kabbitmq"
            from(components["java"])

            artifact(tasks["kotlinSourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("Ktor rabbitMQ plugin")
                packaging = "jar"
                description.set(".")
                url.set("https://github.com/DamirDenis-Tudor/kabbitmq")

                scm {
                    connection.set("scm:git:https://github.com/DamirDenis-Tudor/kabbitmq.git")
                    developerConnection.set("scm:git:git@github.com:DamirDenis-Tudor/kabbitmq.git")
                    url.set("https://github.com/DamirDenis-Tudor/kabbitmq")
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
        maven{
            url = uri("$projectDir/build/publish")
        }
    }
}



signing {
    sign(publishing.publications["kotlin"])
}

tasks.register<Zip>("zipBuildFolder") {
    dependsOn(tasks["clean"])
    dependsOn("publish")

    from("$projectDir/build/publish")

    archiveFileName.set("io.zip")
    destinationDirectory.set(file("$projectDir/build"))
}

tasks.register("uploadArtifact") {
    dependsOn("zipBuildFolder")

    doLast {
        val zipFile = file("$projectDir/build/io.zip")
        val base64Token = project.findProperty("tokenBase64")?.toString() ?: ""

        println("Uploading artifact...")

        val output = ByteArrayOutputStream()
        exec {
            commandLine(
                "curl", "--silent", "--request", "POST",
                "--header", "Authorization: Bearer $base64Token",
                "--form", "bundle=@${zipFile.absolutePath}",
                "https://central.sonatype.com/api/v1/publisher/upload?name=kabbitmq&publishingType=AUTOMATIC"
            )
            standardOutput = output
        }.let {
            if (it.exitValue != 0) {
                println("Upload failed. Exiting...")
                return@doLast
            }
        }

        println("Artifact successfully uploaded...")

        val uploadId = output.toString(Charsets.UTF_8)
        while (true) {
            runCatching {
                println("Checking upload status...")
                with(ByteArrayOutputStream()) {
                    exec {
                        commandLine(
                            "curl", "--silent", "--request", "POST",
                            "--url", "https://central.sonatype.com/api/v1/publisher/status?id=$uploadId",
                            "--header", "accept: application/json",
                            "--header", "Authorization: Bearer $base64Token"
                        )
                        standardOutput = this@with
                    }.let {
                        if (it.exitValue != 0) {
                            println("Deployment not published ...")
                            return@doLast
                        }
                    }

                     with(this@with.toString(Charsets.UTF_8).apply(::println)) {
                        when {
                            contains("PUBLISHED") ->  Thread.sleep(1_000).apply { return@doLast }
                            contains("FAILED") -> Thread.sleep(1_000).apply { exitProcess(-1) }
                            else -> Thread.sleep(60_000)
                        }
                    }
                }
            }.getOrElse { e ->
                println("Error while checking status: ${e.message}")
                Thread.sleep(1000).apply { exitProcess(-1) }
            }
        }
    }
}