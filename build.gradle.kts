import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import com.palantir.gradle.gitversion.VersionDetails
import net.fabricmc.loom.task.RemapJarTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val minecraftVersion: String by project
val curseProjectId: String by project
val curseMinecraftVersion: String by project
val modJarBaseName: String by project
val modMavenGroup: String by project

plugins {
    java
    kotlin("jvm") version "1.3.61"
    idea
    `maven-publish`
    id("fabric-loom") version "0.2.7-SNAPSHOT"
    id("com.palantir.git-version") version "0.12.3"
    id("com.matthewprenger.cursegradle") version "1.4.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

base {
    archivesBaseName = modJarBaseName
}

repositories {
    mavenCentral()
    jcenter()
    maven(url = "http://maven.fabricmc.net")
}

val gitVersion: groovy.lang.Closure<Any> by extra
val versionDetails: groovy.lang.Closure<VersionDetails> by extra

version = "${gitVersion()}+mc$minecraftVersion"
group = modMavenGroup

minecraft {
}

configurations {
    listOf(mappings, modCompile, include).forEach {
        it {
            resolutionStrategy.activateDependencyLocking()
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$minecraftVersion+build.14:v2")
    modImplementation("net.fabricmc:fabric-loader:0.8.2+build.194")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.5.1+build.294-1.15")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.3.61+build.1")

    modImplementation("me.shedaniel.cloth:config-2:2.12")
    modImplementation("me.sargunvohra.mcmods:autoconfig1u:2.0")

    include("me.shedaniel.cloth:config-2:2.12")
    include("me.sargunvohra.mcmods:autoconfig1u:2.0")

    modRuntime("io.github.prospector:modmenu:1.10.2+build.32")
}


val processResources = tasks.getByName<ProcessResources>("processResources") {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        filter { line -> line.replace("%VERSION%", "${project.version}") }
    }
}

val javaCompile = tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val jar = tasks.getByName<Jar>("jar") {
    from("LICENSE")
}

val remapJar = tasks.getByName<RemapJarTask>("remapJar")

if (versionDetails().isCleanTag) {

    curseforge {
        if (project.hasProperty("curseforge_api_key")) {
            apiKey = project.property("curseforge_api_key")!!
        }

        project(closureOf<CurseProject> {
            id = curseProjectId
            changelog = file("changelog.txt")
            releaseType = "release"
            addGameVersion(curseMinecraftVersion)
            addGameVersion("Fabric")
            relations(closureOf<CurseRelation>{
                requiredDependency("fabric-api")
                requiredDependency("fabric-language-kotlin")
                embeddedLibrary("cloth-config")
                embeddedLibrary("auto-config-updated-api")
                optionalDependency("health-overlay")
            })
            mainArtifact(file("${project.buildDir}/libs/${base.archivesBaseName}-$version.jar"))
            afterEvaluate {
                mainArtifact(remapJar)
                uploadTask.dependsOn(remapJar)
            }
        })

        options(closureOf<Options> {
            forgeGradleIntegration = false
        })
    }

}
