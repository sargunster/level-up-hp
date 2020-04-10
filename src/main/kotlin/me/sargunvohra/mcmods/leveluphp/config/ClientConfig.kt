package me.sargunvohra.mcmods.leveluphp.config

import me.sargunvohra.mcmods.autoconfig1u.ConfigData
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry

@Config(name = "leveluphp")
@Config.Gui.Background("textures/block/spruce_planks.png")
data class ClientConfig(
    @ConfigEntry.Gui.PrefixText
    val enableXpBarOverride: Boolean = true
) : ConfigData {
    override fun validatePostLoad() {}
}
