package me.sargunvohra.leveluphp

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import me.sargunvohra.leveluphp.LuhpCapabilities.LUHP_DATA
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.SoundCategory
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerEvent

object LuhpEventHandler {

    private fun initData(player: EntityPlayerMP) {
        player.luhpData.initialized = true
        player.luhpXp = 0
        player.luhpLevel = 0
        player.health = player.maxHealth
    }

    @SubscribeEvent
    fun onPlayerLoad(event: PlayerEvent.LoadFromFile) {
        val player = event.entityPlayer as? EntityPlayerMP ?: return
        val data = player.luhpData

        if (!data.initialized)
            initData(player)
        else
            player.luhpSync()
    }

    @SubscribeEvent
    fun onPlayerClone(event: PlayerEvent.Clone) {
        val newPlayer = event.entityPlayer as? EntityPlayerMP ?: return

        if (event.isWasDeath) {
            if (!LuhpConfig.resetOnDeath) {
                val oldPlayer = event.original as? EntityPlayerMP ?: return
                LUHP_DATA.readNBT(newPlayer.luhpData, null, LUHP_DATA.writeNBT(oldPlayer.luhpData, null))
                newPlayer.luhpXp -= newPlayer.penaltyLuhpXp
            } else {
                initData(newPlayer)
            }
        }

        newPlayer.luhpSync()

        if (event.isWasDeath)
            newPlayer.health = newPlayer.maxHealth
    }

    @SubscribeEvent
    fun onPlayerChangeDim(event: net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent) {
        (event.player as? EntityPlayerMP ?: return).luhpSync()
    }

    @SubscribeEvent
    fun onPlayerKill(event: LivingDeathEvent) {
        val source = event.source.entity as? EntityPlayerMP ?: return
        val oldLevel = source.luhpLevel

        when (event.entity) {
            is EntityMob -> source.luhpXp += LuhpConfig.monsterGain
            is EntityAnimal -> source.luhpXp += LuhpConfig.livestockGain
        }

        if (source.luhpLevel > oldLevel) {

            source.world.playSound(null, source.posX, source.posY, source.posZ,
                    LuhpSounds.levelUp, SoundCategory.PLAYERS, 1f, 1f)

            source.sendStatusMsg("§c§lHP UP!")

            if (LuhpConfig.healOnLevelUp)
                source.health = source.maxHealth
        }
    }
}