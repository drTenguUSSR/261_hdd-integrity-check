plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.verifier"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Тестирование
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Логирование (опционально)
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Библиотеки для работы с хешами (альтернатива стандартной MessageDigest)
    // implementation("org.bouncycastle:bcprov-jdk18on:1.77")
}

application {
    mainClass.set("com.verifier.Main")
}

// Настройки JVM для запуска
tasks.named<JavaExec>("run") {
    jvmArgs = listOf(
        "-XX:+UseParallelGC",
        "-Xms256m",
        "-Xmx512m",
        "-XX:+HeapDumpOnOutOfMemoryError"
    )
}

// Настройки сборки JAR
tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.verifier.Main",
            "Implementation-Version" to version
        )
    }
}

// Shadow JAR для создания fat-jar со всеми зависимостями
tasks.shadowJar {
    archiveBaseName.set("LargeFileVerifier")
    archiveClassifier.set("")
    manifest {
        attributes("Main-Class" to "com.verifier.Main")
    }
}

// Тесты
tasks.test {
    useJUnitPlatform()
    jvmArgs = listOf("-Xmx512m")
}

// Задачи для сборки дистрибутива
tasks.register<Copy>("copyDist") {
    dependsOn(tasks.shadowJar)
    from(tasks.shadowJar.map { it.archiveFile })
    into("dist")
}

// Native compilation (опционально, требует GraalVM)
// plugins {
//     id("org.graalvm.buildtools.native") version "0.10.1"
// }
// graalvmNative {
//     binaries {
//         named("main") {
//             imageName.set("LargeFileVerifier")
//         }
//     }
// }
