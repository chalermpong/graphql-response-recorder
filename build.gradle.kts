import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation

plugins {
    application
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
    id("com.apollographql.apollo3").version("3.1.0")
}

group = "cli"
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(Testing.junit.params)
    testRuntimeOnly(Testing.junit.engine)
}

application {
    mainClass.set("cli.JvmMainKt")
}

apollo {
    packageName.set("cli")

    useSemanticNaming.set(true)

}

kotlin {

    val jvmTarget = jvm()

    sourceSets {
        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }

        val commonMain by getting {
            dependencies {
                implementation("com.github.ajalt.clikt:clikt:_")
                implementation("com.github.ajalt.mordant:mordant:_")
                //implementation("com.squareup.okio:okio-multiplatform:_")
                implementation("com.squareup.okio:okio:3.0.0")
                implementation(KotlinX.coroutines.core)

                implementation("com.apollographql.apollo3:apollo-runtime:3.1.0")
                implementation("com.apollographql.apollo3:apollo-api:3.1.0")

                /// implementation(Ktor.client.core)
                /// implementation(Ktor.client.serialization)
                implementation(KotlinX.serialization.core)
                implementation(KotlinX.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Kotlin.test.common)
                implementation(Kotlin.test.annotationsCommon)
            }
        }
        getByName("jvmMain") {
            dependsOn(commonMain)
            dependencies {
                implementation(Ktor.client.okHttp)
                /// implementation(Square.okHttp3.okHttp)
            }
        }
        getByName("jvmTest") {
            dependencies {
                implementation(Testing.junit.api)
                implementation(Testing.junit.engine)
                implementation(Kotlin.test.junit5)
            }
        }

        sourceSets {
            all {
                languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
                languageSettings.useExperimentalAnnotation("okio.ExperimentalFileSystem")
            }
        }
    }

    tasks.withType<JavaExec> {
        // code to make run task in kotlin multiplatform work
        val compilation = jvmTarget.compilations.getByName<KotlinJvmCompilation>("main")

        val classes = files(
            compilation.runtimeDependencyFiles,
            compilation.output.allOutputs
        )
        classpath(classes)
    }
    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveBaseName.set(project.name)
        archiveClassifier.set("")
        archiveVersion.set("")

        from(jvmTarget.compilations.getByName("main").output)
        configurations = mutableListOf(
            jvmTarget.compilations.getByName("main").compileDependencyFiles as Configuration,
            jvmTarget.compilations.getByName("main").runtimeDependencyFiles as Configuration
        )
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

interface Injected {
    @get:Inject
    val exec: ExecOperations
    @get:Inject
    val fs: FileSystemOperations
}
