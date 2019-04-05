package me.sargunvohra.mcmods.leveluphp.config

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object ClientConfigManager {

    var config = ClientConfig()
        private set

    fun load() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val file = File(FabricLoader.getInstance().configDirectory, "leveluphp.json")
        if (!file.exists()) {
            file.createNewFile()
        } else {
            gson.fromJson(FileReader(file), ClientConfig::class.java)?.let {
                it.validate()
                this.config = it
            }
        }
        val writer = FileWriter(file)
        gson.toJson(config, writer)
        writer.close()
    }
}
