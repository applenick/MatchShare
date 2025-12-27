import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("buildlogic.java-conventions")
    `maven-publish`
    id("com.gradleup.shadow")
}

dependencies {
    compileOnly("dev.pgm.paper:paper-api:1.8_1.21.11-SNAPSHOT")

    implementation(project(":util"))

    runtimeOnly(project(":platform-sportpaper")) { exclude("*") }
    runtimeOnly(project(":platform-modern")) { exclude("*") }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveFileName = "MatchShare.jar"
    archiveClassifier.set("")
    destinationDirectory = rootProject.projectDir.resolve("build/libs")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    minimize {
        // Exclude from minimization as they're required at runtime
        exclude(project(":platform-sportpaper"))
        exclude(project(":platform-modern"))
    }

    dependencies {
        exclude(dependency("org.jpspecify:jspecify"))
        exclude(dependency("org.jetbrains:annotations"))
    }

    exclude("META-INF/**")
}

publishing {
    publications.create<MavenPublication>("MatchShare") {
        groupId = project.group as String
        artifactId = project.name
        version = project.version as String

        artifact(tasks["shadowJar"])
    }
}

tasks {
    processResources {
        val description = project.description
        val version = project.version.toString()
        val commitHash = project.latestCommitHash()

        filesMatching(listOf("plugin.yml")) {
            expand(
                mapOf(
                    "description" to description,
                    "apiVersion" to "1.21.11",
                    "mainClass" to "tc.oc.occ.matchshare.MatchShare",
                    "version" to version,
                    "commitHash" to commitHash,
                    "author" to "applenick"
                )
            )
        }
    }

    named("jar") {
        enabled = false
    }

    named("build") {
        dependsOn(shadowJar)
    }
}