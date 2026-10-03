plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "it.unicam.cs.mpgc"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

javafx {
    version = "21.0.4"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    // JSON per la persistenza
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.1")
    // Test
    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    // Classe che contiene il metodo main
    mainClass.set("it.unicam.cs.mpgc.rpg122627.Main")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        override fun afterSuite(desc: TestDescriptor, result: TestResult) {
            if (desc.parent == null) {
                val total = result.testCount
                val passed = result.successfulTestCount
                val failed = result.failedTestCount
                val skipped = result.skippedTestCount
                val duration = "%.2f".format((result.endTime - result.startTime) / 1000.0)
                println("")
                println("┌──────────────────────────────────────────────┐")
                println("│  Test results: ${result.resultType}")
                println("│  Total: $total   Passed: $passed   Failed: $failed   Skipped: $skipped")
                println("│  Duration: ${duration}s")
                println("└──────────────────────────────────────────────┘")
            }
        }
    })
}