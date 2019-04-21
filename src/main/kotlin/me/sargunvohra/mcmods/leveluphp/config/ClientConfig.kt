package me.sargunvohra.mcmods.leveluphp.config

import me.sargunvohra.mcmods.autoconfig.api.ConfigData
import me.sargunvohra.mcmods.autoconfig.api.ConfigGuiEntry

data class ClientConfig (
    @ConfigGuiEntry
    val enableXpBarOverride: Boolean = true
) : ConfigData {
    override fun validatePostLoad() {}
}
