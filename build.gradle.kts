import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
    kotlin("jvm") version "1.2.41"
    java
}

repositories {
    jcenter()
    maven("https://jitpack.io")
}

dependencies {
    compile(kotlin("stdlib"))
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:0.22.5")
    compile("com.github.kittinunf.fuel:fuel:+")
    compile("org.jsoup:jsoup:1.11.3")
    compile("net.sf.sevenzipjbinding:sevenzipjbinding:9.20-2.00beta")
    compile("net.sf.sevenzipjbinding:sevenzipjbinding-all-windows:9.20-2.00beta")
    compile("org.tukaani:xz:+")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.1.0")
}

kotlin {
    experimental {
        coroutines = Coroutines.ENABLE
    }
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = "${project.name}-fat"
    manifest {
        attributes["Implementation-Title"] = "Emulator Downloader"
        attributes["Implementation-Version"] = "0.1"
        attributes["Main-Class"] = "com.github.alondero.emulatordownloader.MainKt"
    }
    from(configurations.runtime.map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks["jar"] as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}