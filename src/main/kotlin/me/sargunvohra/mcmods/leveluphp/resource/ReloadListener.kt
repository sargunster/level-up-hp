package me.sargunvohra.mcmods.leveluphp.resource

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.sargunvohra.mcmods.leveluphp.LevelUpHp
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import org.apache.logging.log4j.LogManager
import java.io.InputStreamReader

class ReloadListener : SimpleSynchronousResourceReloadListener {

    private val gson = Gson()
    var config: LevellingConfig = LevellingConfig()
        private set

    init {
        config.validate()
    }

    override fun apply(resourceManager: ResourceManager) {
        val overrides = resourceManager
            .findResources("leveluphp/override") { it.endsWith(".json") }
            .fold(HashMap<String, Int>()) { map, resource ->
                val override: Map<String, Int> = gson.fromJson(
                    InputStreamReader(resourceManager.getResource(resource).inputStream),
                    object : TypeToken<Map<String, Int>>() {}.type
                )
                map += override
                return@fold map
            }

        val general = resourceManager.getResource(LevelUpHp.id("leveluphp/general.json"))
        val config = gson.fromJson(
            InputStreamReader(general.inputStream),
            LevellingConfig::class.java
        ).copy(overrides = overrides)

        LogManager.getLogger().info("Loaded {}", config)

        config.validate()
        this.config = config
    }

    override fun getFabricId() = LevelUpHp.id("reload_listener")
}
