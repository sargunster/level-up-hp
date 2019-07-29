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
    kotlin("jvm") version "1.3.40"
    idea
    `maven-publish`
    id("fabric-loom") version "0.2.3-SNAPSHOT"
    id("com.palantir.git-version") version "0.11.0"
    id("com.matthewprenger.cursegradle") version "1.2.0"
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
    maven(url = "https://minecraft.curseforge.com/api/maven")
    maven(url = "http://maven.sargunv.s3-website-us-west-2.amazonaws.com/")
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
    mappings("net.fabricmc:yarn:$minecraftVersion+build.3")
    modCompile("net.fabricmc:fabric-loader:0.4.8+")

    modCompile("net.fabricmc.fabric-api:fabric-api:0.3.0+build.206")
    modCompile("net.fabricmc:fabric-language-kotlin:1.3.40+")

    modCompile("cloth-config:ClothConfig:0.2.4.17")
    modCompile("me.sargunvohra.mcmods:auto-config:1.2.+")

    include("cloth-config:ClothConfig:0.2.4.17")
    include("me.sargunvohra.mcmods:auto-config:1.2.+")

    modCompile("io.github.prospector.modmenu:ModMenu:1.+")
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
            relations(closureOf<CurseRelation>{
                requiredDependency("fabric-api")
                requiredDependency("fabric-language-kotlin")
                embeddedLibrary("cloth-config")
                embeddedLibrary("auto-config")
                optionalDependency("health-overlay")
            })
            mainArtifact(file("${project.buildDir}/libs/${base.archivesBaseName}-$version.jar"))
            afterEvaluate {
                uploadTask.dependsOn(remapJar)
            }
        })

        options(closureOf<Options> {
            forgeGradleIntegration = false
        })
    }

}
