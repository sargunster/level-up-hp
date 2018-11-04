package me.sargunvohra.leveluphp

import me.sargunvohra.leveluphp.commands.CommandHp
import me.sargunvohra.leveluphp.commands.CommandLuhp
import me.sargunvohra.leveluphp.constants.MOD_ID
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.shadowfacts.shadowmc.config.ForgeConfigAdapter


@Suppress("unused")
@Mod(
        modid = MOD_ID,
        useMetadata = true,
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
        guiFactory = "me.sargunvohra.leveluphp.ModGuiFactory"
)
object LevelUpHpMod {

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        ForgeConfigAdapter.init()
        ModConfig.init(event.suggestedConfigurationFile)
        ModConfig.load()

        CapabilityManager.INSTANCE.register(
                LuhpData::class.java,
                LuhpData.Storage(),
                LuhpData.Impl::class.java::newInstance
        )

        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(EventHandler)
    }

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        event.registerServerCommand(CommandLuhp)
        event.registerServerCommand(CommandHp)
    }

    @SubscribeEvent
    fun registerSounds(event: RegistryEvent.Register<SoundEvent>) {
        event.registry.register(Sounds.levelUp)
    }

    @SubscribeEvent
    fun onEntityAttachCapability(event: AttachCapabilitiesEvent<Entity>) {
        val obj = event.`object`
        if (obj is EntityPlayerMP) {
            event.addCapability(ResourceLocation(MOD_ID, LuhpData::class.java.simpleName), LuhpData.Provider())
        }
    }

    @SubscribeEvent
    fun onConfigChanged(event: ConfigChangedEvent.OnConfigChangedEvent) {
        if (event.modID.toLowerCase() == MOD_ID) {
            ModConfig.load()
        }
    }
}