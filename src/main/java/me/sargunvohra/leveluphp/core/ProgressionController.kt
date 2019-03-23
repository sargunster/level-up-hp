package me.sargunvohra.leveluphp.core

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

/** This class is responsible for managing progression of the player's hp level.  */
class ProgressionController {

    @SubscribeEvent
    internal fun onLivingDeath(event: LivingDeathEvent) {
        val source = event.source.trueSource as? EntityPlayer ?: return
        source.playerLevelHandler.ifPresent {
            val oldLevel = it.level
            it.xp += 1
            if (it.level > oldLevel)
                source.sendMessage(TextComponentString("Level up!"))
        }
    }

    fun register() {
        MinecraftForge.EVENT_BUS.register(this)
    }
}
