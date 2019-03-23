package me.sargunvohra.leveluphp.level

import net.minecraft.entity.player.EntityPlayer

val EntityPlayer.playerLevelHandler
    get() = getCapability(LevellerLoader.CAPABILITY)
