import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import net.minecraftforge.gradle.common.util.MinecraftExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val minecraftVersion: String by project
val curseProjectId: String by project
val curseMinecraftVersion: String by project
val modJarBaseName: String by project
val modMavenGroup: String by project
val modPlatform: String by project

buildscript {
    repositories {
        maven(url = "https://files.minecraftforge.net/maven")
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath(group="net.minecraftforge.gradle", name="ForgeGradle", version="3.+")
                .setChanging(true)
                .exclude(group="trove", module = "trove")
    }
}

plugins {
    java
    kotlin("jvm") version "1.3.61"
    idea
    id("com.matthewprenger.cursegradle") version "1.4.0"
}

apply(plugin="net.minecraftforge.gradle")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

base {
    archivesBaseName = "$modJarBaseName-mc$minecraftVersion-$modPlatform"
}

repositories {
    maven(url = "https://files.minecraftforge.net/maven")
    mavenCentral()
    jcenter()
    maven(url= "https://minecraft.curseforge.com/api/maven/")
}

version = "5.1.1"
group = modMavenGroup

configure<MinecraftExtension> {
    mappings("snapshot", "20200119-1.14.4")
//    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        create("client")
        create("server")
        create("data") {
            args("--mod", "leveluphp", "--all", "--output", file("src/generated/resources"))
        }

        configureEach {
            workingDirectory(project.file("run/$name"))
            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            property("forge.logging.console.level", "debug")

            mods {
                create("leveluphp") {
                    source(sourceSets["main"])
                }
            }
        }
    }
}

dependencies {
    "minecraft"("net.minecraftforge:forge:$minecraftVersion-28.2.5")
    implementation("kottle:Kottle:1.4.0")
}

curseforge {
    if (project.hasProperty("curseforge_api_key")) {
        apiKey = project.property("curseforge_api_key")!!
    }

    project(closureOf<CurseProject> {
        id = curseProjectId
        releaseType = "release"
        addGameVersion(curseMinecraftVersion)
        addGameVersion(modPlatform.capitalize())
        relations(closureOf<CurseRelation>{
            requiredDependency("kottle")
        })
        options(closureOf<Options> {
            forgeGradleIntegration = (modPlatform == "forge")
        })
    })
}
