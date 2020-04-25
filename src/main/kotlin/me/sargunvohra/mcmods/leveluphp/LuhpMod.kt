package me.sargunvohra.mcmods.leveluphp

import me.sargunvohra.mcmods.leveluphp.advancement.LevelUpCriterion
import me.sargunvohra.mcmods.leveluphp.command.buildLevelUpHpCommand
import me.sargunvohra.mcmods.leveluphp.config.ClientConfig
import me.sargunvohra.mcmods.leveluphp.config.LevellingConfigLoader
import me.sargunvohra.mcmods.leveluphp.gui.renderLuhpExpBars
import me.sargunvohra.mcmods.leveluphp.item.HeartContainerItem
import me.sargunvohra.mcmods.leveluphp.level.IHpLeveller
import me.sargunvohra.mcmods.leveluphp.network.SyncPacketConsumer
import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.Item
import net.minecraft.network.PacketBuffer
import net.minecraft.util.SoundEvent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.event.entity.living.LivingDropsEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkRegistry
import java.util.*

@Mod("leveluphp")
object LuhpMod {
    private const val protocolVersion = "1"
    val channel = NetworkRegistry.newSimpleChannel(
        LuhpIds.LEVEL_SYNC_CHANNEL,
        { protocolVersion },
        { it == protocolVersion },
        { it == protocolVersion || it == "ABSENT" }
    )!!
    val levelUpSound = SoundEvent(LuhpIds.LEVEL_UP_SOUND)
        .setRegistryName(LuhpIds.LEVEL_UP_SOUND.path)!!
    private val heartContainerItem = HeartContainerItem()
        .setRegistryName(LuhpIds.HEART_CONTAINER_ITEM)!!

    init {
        @Suppress("INACCESSIBLE_TYPE")
        channel.registerMessage(
            0,
            PacketBuffer::class.java,
            { message, packetBuffer -> packetBuffer.writeBytes(message) },
            { packetBuffer -> packetBuffer.buffer },
            { message, context -> SyncPacketConsumer.accept(message, context.get()) },
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        )
        ModLoadingContext.get().registerConfig(
            ModConfig.Type.CLIENT,
            ClientConfig.spec,
            "leveluphp.toml"
        )
        CriteriaTriggers.register(LevelUpCriterion)

        FMLKotlinModLoadingContext.get().modEventBus.register(this)
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onSetup(event: FMLCommonSetupEvent) {
        event.description()
        CapabilityManager.INSTANCE.register(
            IHpLeveller::class.java,
            IHpLeveller.Storage,
            IHpLeveller::Impl
        )
    }

    @SubscribeEvent
    fun onServerAboutToStart(event: FMLServerAboutToStartEvent) {
        event.server.resourceManager.addReloadListener(LevellingConfigLoader(event.server))
    }

    @SubscribeEvent
    fun onServerStarting(event: FMLServerStartingEvent) {
        event.commandDispatcher.register(buildLevelUpHpCommand())
    }

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        event.registry.register(heartContainerItem)
    }

    @SubscribeEvent
    fun registerSounds(event: RegistryEvent.Register<SoundEvent>) {
        event.registry.register(levelUpSound)
    }

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

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun onRenderExpBar(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE)
            return
        if (!ClientConfig.instance.enableXpBarOverride)
            return
        val client = Minecraft.getInstance()
        val player = client.player ?: return
        event.isCanceled = true
        renderLuhpExpBars(client, player)
    }
}
