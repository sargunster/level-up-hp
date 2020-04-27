package me.sargunvohra.mcmods.leveluphp.core

import me.sargunvohra.mcmods.leveluphp.LuhpIds
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.living.LivingDropsEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
object HpLevellerLifecycleSubscriber {
    @SubscribeEvent
    fun onAttachEntityCapabilities(event: AttachCapabilitiesEvent<Entity>) {
        val entity = event.`object`
        if (entity is PlayerEntity) {
            event.addCapability(LuhpIds.LEVELLER_CAPABILITY, PlayerAttachedHpLeveller().attachTo(entity))
        }
    }

    @SubscribeEvent
    fun onClone(event: PlayerEvent.Clone) {
        val newPlayer = event.player
        val newLeveller = newPlayer.hpLeveller
        val oldLeveller = event.original.hpLeveller

        newLeveller.restoreTo(level = oldLeveller.level, xp = oldLeveller.xp)
        if (event.isWasDeath) {
            newLeveller.handleDeath()
            newPlayer.health = newPlayer.maxHealth
        } else {
            newPlayer.health = event.original.health
        }
    }

    @SubscribeEvent
    fun onChangeDimension(event: PlayerEvent.PlayerChangedDimensionEvent) {
        event.player.let {
            it.hpLeveller.updateState()
            // lol
            it.health -= 1
            it.health += 1
        }
    }

    @SubscribeEvent
    fun onRespawn(event: PlayerEvent.PlayerRespawnEvent) {
        event.player.hpLeveller.updateState()
    }

    @SubscribeEvent
    fun onConnect(event: PlayerEvent.PlayerLoggedInEvent) {
        event.player.hpLeveller.updateState()
    }

    @SubscribeEvent
    fun onDrop(event: LivingDropsEvent) {
        val attacker = event.entityLiving.attackingEntity
        if (event.isRecentlyHit && attacker is ServerPlayerEntity) {
            attacker.hpLevellerOrNull?.handleKillEnemy(event.entity)
        }
    }
}
