package me.sargunvohra.mcmods.leveluphp.capability

import me.sargunvohra.mcmods.leveluphp.LuhpIds
import me.sargunvohra.mcmods.leveluphp.hpLevelHandler
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.entity.living.LivingDropsEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
object PlayerLifecycleSubscriber {
    @SubscribeEvent
    fun onAttachEntityCapabilities(event: AttachCapabilitiesEvent<Entity>) {
        val entity = event.`object`
        if (entity is PlayerEntity) {
            event.addCapability(LuhpIds.LEVELLER_CAPABILITY, IHpLeveller.Provider(entity))
        }
    }

    @SubscribeEvent
    fun onClone(event: PlayerEvent.Clone) {
        val newPlayer = event.player
        val newHandler = newPlayer.hpLevelHandler
        val oldHandler = event.original.hpLevelHandler

        newHandler.copyFrom(oldHandler)
        if (event.isWasDeath) {
            newHandler.applyDeathPenalty()
            newPlayer.health = newPlayer.maxHealth
        }
    }

    @SubscribeEvent
    fun onChangeDimension(event: PlayerEvent.PlayerChangedDimensionEvent) {
        event.player.let {
            it.hpLevelHandler.onModified()
            // lol
            it.health -= 1
            it.health += 1
        }
    }

    @SubscribeEvent
    fun onRespawn(event: PlayerEvent.PlayerRespawnEvent) {
        event.player.hpLevelHandler.onModified()
    }

    @SubscribeEvent
    fun onConnect(event: PlayerEvent.PlayerLoggedInEvent) {
        event.player.hpLevelHandler.onModified()
    }

    @SubscribeEvent
    fun onDrop(event: LivingDropsEvent) {
        val attacker = event.entityLiving.attackingEntity
        if (event.isRecentlyHit && attacker is ServerPlayerEntity) {
            attacker.hpLevelHandler.applyKill(event.entity)
        }
    }
}
