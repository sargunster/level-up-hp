package me.sargunvohra.mcmods.leveluphp.config

data class ClientConfig(
    val disableXpBarOverlay: Boolean = false
) {
    fun validate() {}
}
