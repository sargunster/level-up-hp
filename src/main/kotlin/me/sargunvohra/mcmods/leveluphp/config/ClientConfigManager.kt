package me.sargunvohra.mcmods.leveluphp.config

import com.google.gson.GsonBuilder
import me.shedaniel.cloth.gui.ClothConfigScreen
import me.shedaniel.cloth.gui.entries.BooleanListEntry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Screen
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object ClientConfigManager {

    var config = ClientConfig()
        private set

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val file = File(FabricLoader.getInstance().configDirectory, "leveluphp.json")

    fun init() {
        if (!file.exists()) {
            file.createNewFile()
        } else {
            load()
        }
        save() // save in either case because we want to add any new keys
        registerConfigButton()
    }

    private fun load() {
        gson.fromJson(FileReader(file), ClientConfig::class.java)?.let {
            it.validate()
            this.config = it
        }
    }

    private fun save() {
        val writer = FileWriter(file)
        gson.toJson(config, writer)
        writer.close()
    }

    private fun registerConfigButton() {
        if (!FabricLoader.getInstance().isModLoaded("modmenu"))
            return

        Class.forName("io.github.prospector.modmenu.api.ModMenuApi")
            .getMethod("addConfigOverride", String::class.java, Runnable::class.java)
            .invoke(null, "leveluphp", Runnable {
                openConfigScreen(MinecraftClient.getInstance().currentScreen!!)
            })
    }

    private fun openConfigScreen(parent: Screen) {
        val builder = ClothConfigScreen.Builder(parent, "text.leveluphp.config.title") {
            save()
        }
        val default = ClientConfig()

        builder.addCategory("text.leveluphp.config.category.general").addOption(
            BooleanListEntry(
                "text.leveluphp.config.option.disableXpBarOverlay",
                config.disableXpBarOverlay,
                "text.cloth.reset_value",
                { default.disableXpBarOverlay },
                { config = config.copy(disableXpBarOverlay = it) }
            )
        )

        MinecraftClient.getInstance().openScreen(builder.build())
    }
}
