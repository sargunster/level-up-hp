import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import com.palantir.gradle.gitversion.VersionDetails
import net.fabricmc.loom.task.RemapJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val minecraftVersion: String by project
val yarnMappings: String by project
val loaderVersion: String by project

val curseProjectId: String by project
val curseMinecraftVersion: String by project
val basePackage: String by project
val modJarBaseName: String by project
val modMavenGroup: String by project

val kotlinVersion: String by project
val fabricVersion: String by project
val fabricKotlinVersion: String by project
val clothConfigVersion: String by project
val autoConfigVersion: String by project
val modMenuVersion: String by project

plugins {
    java
    kotlin("jvm") version "1.3.30"
    idea
    `maven-publish`
    id("fabric-loom") version "0.2.1-SNAPSHOT"
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
    maven(url = "https://maven.fabricmc.net/io/github/prospector/modmenu/ModMenu/")
}

val gitVersion: groovy.lang.Closure<Any> by extra
val versionDetails: groovy.lang.Closure<VersionDetails> by extra

version = "${gitVersion()}+mc$minecraftVersion"
group = modMavenGroup

minecraft {
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$minecraftVersion+$yarnMappings")
    modCompile("net.fabricmc:fabric-loader:$loaderVersion")

    modCompile("net.fabricmc:fabric:$fabricVersion")
    include("net.fabricmc:fabric:$fabricVersion")

    modCompile("net.fabricmc:fabric-language-kotlin:$fabricKotlinVersion")
    compileOnly(kotlin("stdlib-jdk8", kotlinVersion))
    include("net.fabricmc:fabric-language-kotlin:$fabricKotlinVersion")

    modCompile("cloth-config:ClothConfig:$clothConfigVersion")
    include("cloth-config:ClothConfig:$clothConfigVersion")

    modCompile("me.sargunvohra.mcmods:auto-config:$autoConfigVersion")
    include("me.sargunvohra.mcmods:auto-config:$autoConfigVersion")

    modCompile("io.github.prospector.modmenu:ModMenu:$modMenuVersion")
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

val remapJar = tasks.getByName<RemapJar>("remapJar")

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
                embeddedLibrary("fabric")
                embeddedLibrary("fabric-language-kotlin")
                embeddedLibrary("cloth-config")
                embeddedLibrary("auto-config")
            })
        })

        options(closureOf<Options> {
            forgeGradleIntegration = false
        })
    }

    afterEvaluate {
        tasks.getByName("curseforge$curseProjectId").dependsOn(remapJar)
    }

}
