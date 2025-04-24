private val modId: String by project
private val modName: String by project
private val modVersion: String by project
private val modGroupId: String by project

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.blossom)
    alias(libs.plugins.modDevGradle)
    alias(libs.plugins.gradleIdeaExt)
}

group = modGroupId
version = modVersion

configurations {
    val localRuntime by configurations.creating

    configurations.named("runtimeClasspath") {
        extendsFrom(localRuntime)
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    wrapper {
        distributionType = Wrapper.DistributionType.BIN
    }

    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}

base {
    archivesName = modId
}

kotlin {
    jvmToolchain(
        libs.versions.java
            .get()
            .toInt(),
    )
}

neoForge {
    version = libs.versions.neoforge.get()

    parchment {
        mappingsVersion = libs.versions.parchment.get()
        minecraftVersion = libs.versions.minecraft.get()
    }

    runs {
        register("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }
        register("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }
        register("data") {
            data()
            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file("src/generated/resources").absolutePath,
                "--existing",
                file("src/main/resources").absolutePath,
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }

    ideSyncTask(tasks.processResources)
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content { includeGroup("thedarkcolour") }
    }
}

dependencies {
    implementation(libs.kotlinforforge)
}

sourceSets {
    main {
        blossom {
            val modLicense: String by project
            val modAuthors: String by project
            val modDescription: String by project
            val replacements =
                mapOf(
                    "modId" to modId,
                    "modName" to modName,
                    "modVersion" to modVersion,
                    "modGroupId" to modGroupId,
                    "modLicense" to modLicense,
                    "modAuthors" to modAuthors,
                    "modDescription" to modDescription,
                    "minecraftVersion" to libs.versions.minecraft.get(),
                    "minecraftVersionRange" to libs.versions.minecraftRange.get(),
                    "neoforgeVersion" to libs.versions.neoforge.get(),
                    "neoforgeVersionRange" to libs.versions.neoforgeRange.get(),
                    "loaderVersionRange" to libs.versions.kotlinforforgeRange.get(),
                )
            resources {
                replacements.forEach {
                    property(it.key, it.value)
                }
            }
            kotlinSources {
                replacements.forEach {
                    property(it.key, it.value)
                }
            }
        }
        resources {
            srcDir("src/generated/resources")
        }
    }
}
