package me.sargunvohra.leveluphp

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.shadowfacts.shadowmc.config.ForgeConfigAdapter
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry


const val MOD_ID = "leveluphp"
const val VERSION = "1.0.0"

@Mod(
        modid = MOD_ID,
        version = VERSION,
        dependencies = "required-after:shadowmc@[3.7.1,);",
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter",
        guiFactory = "me.sargunvohra.leveluphp.LevelUpHpConfigGuiFactory")
object LevelUpHpMod {

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        initConfig(event)

        CapabilityManager.INSTANCE.register(LuhpData::class.java, LuhpData.Storage(), LuhpData.Impl::class.java)

        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(LuhpEventHandler)

        GameRegistry.register<IForgeRegistryEntry<*>>(LuhpSounds.levelUp, LuhpSounds.levelUpLoc)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
    }

    @Mod.EventHandler
    fun serverStarting(event: FMLServerStartingEvent) {
        event.registerServerCommand(CommandLuhp)
        event.registerServerCommand(CommandHp)
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
            LuhpConfig.load()
        }
    }

    fun initConfig(event: FMLPreInitializationEvent) {
        ForgeConfigAdapter.init()
        LuhpConfig.init(event.suggestedConfigurationFile)
        LuhpConfig.load()
    }
}