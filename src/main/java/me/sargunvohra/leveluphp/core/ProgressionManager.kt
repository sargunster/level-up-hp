package me.sargunvohra.leveluphp.core

import me.sargunvohra.leveluphp.LevelUpHp
import me.sargunvohra.leveluphp.data.DataPackManager
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

/** This class is responsible for managing progression of the player's hp level.  */
@KotlinEventBusSubscriber(modid = LevelUpHp.MOD_ID)
object ProgressionManager {

    private fun onLevelUp(player: EntityPlayerMP) {
        player.sendStatusMessage(TextComponentString("§c§lHP up!"), true)
    }

    @SubscribeEvent
    internal fun onLivingDeath(event: LivingDeathEvent) {
        if (event.entity.world.isRemote) return

        val source = event.source.trueSource as? EntityPlayerMP
        source?.playerLevelHandler?.ifPresent {
            val value = DataPackManager.config.calculateXpValue(event.entity)
            if (value != 0) {
                val oldLevel = it.level
                it.xp += value
                if (it.level > oldLevel)
                    onLevelUp(source)
            }
        }

        (event.entity as? EntityPlayerMP)?.playerLevelHandler?.ifPresent {
            it.xp -= it.deathPenalty
        }
    }
}
