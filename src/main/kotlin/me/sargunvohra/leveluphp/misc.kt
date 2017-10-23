package me.sargunvohra.leveluphp

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraft.util.text.TextComponentString

fun EntityPlayer.sendStatusMsg(message: String) = sendStatusMessage(TextComponentString(message), true)

@Suppress("MemberVisibilityCanPrivate")
object LuhpSounds {
    val levelUpLoc = ResourceLocation("leveluphp", "level-up")
    val levelUp = SoundEvent(levelUpLoc).setRegistryName(levelUpLoc)
}