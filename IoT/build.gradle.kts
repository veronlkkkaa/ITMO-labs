plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "com.echonode"
version = "0.1.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.3.0")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("com.google.code.gson:gson:2.11.0")
}

application {
    mainClass.set("MainKt")
}

kotlin {
    jvmToolchain(17)
}

tasks.register<JavaExec>("runBot") {
    group = "application"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("MainKt")
}

tasks.register<JavaExec>("runSimulator") {
    group = "application"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("SpeakerSimulatorKt")
}