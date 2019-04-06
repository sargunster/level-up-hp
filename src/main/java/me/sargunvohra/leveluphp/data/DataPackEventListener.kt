package me.sargunvohra.leveluphp.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.sargunvohra.leveluphp.LevelUpHp
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.resources.IResourceManager
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import org.apache.logging.log4j.LogManager
import java.io.InputStreamReader

@KotlinEventBusSubscriber(modid = LevelUpHp.MOD_ID)
object DataPackEventListener {

    private val gson = Gson()
    private val general = LevelUpHp.res("${LevelUpHp.MOD_ID}/general.json")

    var config = LevellingConfig()
        private set

    private fun reload(resourceManager: IResourceManager) {
        val overrides = resourceManager
            .getAllResourceLocations("${LevelUpHp.MOD_ID}/override") {
                it.endsWith(".json")
            }.fold(HashMap<String, Int>()) { map, resource ->
                val override: Map<String, Int> = gson.fromJson(
                    InputStreamReader(resourceManager.getResource(resource).inputStream),
                    object : TypeToken<Map<String, Int>>() {}.type
                )
                map += override
                return@fold map
            }

        config = gson.fromJson(
            InputStreamReader(resourceManager.getResource(general).inputStream),
            LevellingConfig::class.java
        ).copy(overrides = overrides)

        config.validate()

        LogManager.getLogger().info("Loaded: $config")
    }

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
        event.server.resourceManager.addReloadListener(this::reload)
    }
}
