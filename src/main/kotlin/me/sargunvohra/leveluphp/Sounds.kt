package me.sargunvohra.leveluphp

import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent

object Sounds {
    private val levelUpLoc = ResourceLocation("leveluphp", "level-up")
    val levelUp = SoundEvent(levelUpLoc).setRegistryName(levelUpLoc)!!
}