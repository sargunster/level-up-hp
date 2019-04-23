package me.sargunvohra.mcmods.leveluphp

import io.github.prospector.modmenu.api.ModMenuApi
import me.sargunvohra.mcmods.autoconfig1.AutoConfig
import me.sargunvohra.mcmods.leveluphp.config.ClientConfig
import net.minecraft.client.gui.Screen
import java.util.Optional
import java.util.function.Supplier

@Suppress("unused")
class ModMenuCompat : ModMenuApi {
    override fun getModId() = "leveluphp"
    override fun getConfigScreen(screen: Screen): Optional<Supplier<Screen>> {
        return Optional.of(AutoConfig.getConfigScreen(ClientConfig::class.java, screen))
    }
}
