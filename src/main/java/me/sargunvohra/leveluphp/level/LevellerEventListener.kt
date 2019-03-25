package me.sargunvohra.leveluphp.level

import me.sargunvohra.leveluphp.BuildConfig
import me.sargunvohra.leveluphp.LevelUpHp
import me.sargunvohra.svlib.capability.PlayerCapabilityEventListener
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent

@KotlinEventBusSubscriber(modid = LevelUpHp.MOD_ID)
object LevellerEventListener : PlayerCapabilityEventListener<Leveller>(
    capSupplier = { LevellerLoader.CAPABILITY },
    key = LevelUpHp.res("player_level_handler"),
    protocolVersion = BuildConfig.VERSION,
    serverAcceptedVersions = { true }
) {
    @SubscribeEvent
    fun onLivingDeath(event: LivingDeathEvent) {
        if (event.entity.world.isRemote) return

        // apply xp gain if player killed someone
        val source = event.source.trueSource as? EntityPlayerMP
        source?.leveller?.ifPresent {
            it.onKill(event.entity)
        }

        // apply penalties if player got killed
        (event.entity as? EntityPlayerMP)?.leveller?.ifPresent {
            it.xp -= it.xpPenalty
            it.level -= it.levelPenalty
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    fun onServerStarting(event: FMLServerStartingEvent) {
        // when the data pack changes, update leveller stats
        event.server.resourceManager.addReloadListener {
            event.server.playerList.players.forEach { player ->
                player.leveller.ifPresent {
                    it.reconfigure()
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    fun onPlayerClone2(event: PlayerEvent.Clone) {
        // for some reason MC caps health to 20 when returning from the end so let's undo that here
        if (!event.isWasDeath) {
            val newPlayer = event.entityPlayer as? EntityPlayerMP ?: return
            val oldPlayer = event.original as EntityPlayerMP
            newPlayer.health = oldPlayer.health
        }
    }
}
