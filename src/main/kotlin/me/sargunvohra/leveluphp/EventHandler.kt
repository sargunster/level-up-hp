@file:Suppress("unused")

package me.sargunvohra.leveluphp

import me.sargunvohra.leveluphp.Capabilities.LUHP_DATA
import me.sargunvohra.leveluphp.extensions.*
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.passive.IAnimals
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.SoundCategory
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object EventHandler {

    @SubscribeEvent
    fun onPlayerLoad(event: PlayerEvent.LoadFromFile) {
        val player = event.entityPlayer as? EntityPlayerMP ?: return
        val data = player.luhpData

        if (!data.initialized)
            player.luhpInit()
        else
            player.luhpSync()
    }

    @SubscribeEvent
    fun onPlayerClone(event: PlayerEvent.Clone) {
        val newPlayer = event.entityPlayer as? EntityPlayerMP ?: return

        val shouldReset = event.isWasDeath && ModConfig.resetOnDeath

        if (shouldReset) {
            newPlayer.luhpInit()
        } else {
            val oldPlayer = event.original as? EntityPlayerMP ?: return
            LUHP_DATA.readNBT(newPlayer.luhpData, null, LUHP_DATA.writeNBT(oldPlayer.luhpData, null))

            if (event.isWasDeath)
                newPlayer.luhpXp -= newPlayer.penaltyLuhpXp
        }

        newPlayer.luhpSync()

        if (!shouldReset)
            newPlayer.health = newPlayer.maxHealth
    }

    @SubscribeEvent
    fun onPlayerChangeDim(event: net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent) {
        (event.player as? EntityPlayerMP ?: return).luhpSync()
    }

    @SubscribeEvent
    fun onPlayerKill(event: LivingDeathEvent) {
        val source = event.source.trueSource as? EntityPlayerMP ?: return
        val oldLevel = source.luhpLevel

        when (event.entity) {
            is IMob -> source.luhpXp += ModConfig.monsterGain
            is IAnimals -> source.luhpXp += ModConfig.livestockGain
        }

        if (source.luhpLevel > oldLevel) {

            source.world.playSound(null, source.posX, source.posY, source.posZ,
                    Sounds.levelUp, SoundCategory.PLAYERS, 1f, 1f)

            source.sendStatusMsg("§c§lHP UP!")

            if (ModConfig.healOnLevelUp)
                source.health = source.maxHealth
        }
    }
}