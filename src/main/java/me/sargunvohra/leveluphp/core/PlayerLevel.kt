package me.sargunvohra.leveluphp.core

import me.sargunvohra.leveluphp.BuildConfig
import me.sargunvohra.leveluphp.LevelUpHp
import me.sargunvohra.leveluphp.Resources
import me.sargunvohra.leveluphp.data.DataPackManager
import me.sargunvohra.svlib.capability.PlayerCapability
import me.sargunvohra.svlib.capability.PlayerCapabilityController
import net.alexwells.kottle.KotlinEventBusSubscriber
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.INBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import java.util.UUID

@KotlinEventBusSubscriber(modid = LevelUpHp.MOD_ID, bus = KotlinEventBusSubscriber.Bus.MOD)
object PlayerLevel {
    @JvmStatic
    @CapabilityInject(PlayerLevelHandler::class)
    lateinit var CAPABILITY: Capability<PlayerLevelHandler>

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onSetup(event: FMLCommonSetupEvent) {
        CapabilityManager.INSTANCE.register(
            PlayerLevelHandler::class.java,
            PlayerLevelStorage()
        ) { PlayerLevelHandler() }
    }
}

@KotlinEventBusSubscriber(modid = LevelUpHp.MOD_ID)
object PlayerLevelManager : PlayerCapabilityController<PlayerLevelHandler>(
    capSupplier = { PlayerLevel.CAPABILITY },
    key = Resources.get("player_level_handler"),
    protocolVersion = BuildConfig.VERSION
)

class PlayerLevelHandler : PlayerCapability() {
    private var healOnApply = false

    var xp: Int = 0
        set(value) {
            val config = DataPackManager.config
            val maxLevel = config.maximumLevel

            field = value.coerceAtLeast(0)

            while (xp >= xpTarget && level < maxLevel) {
                field -= xpTarget
                level++
                healOnApply = config.healOnLevelUp
            }
            if (level == maxLevel) field = xpTarget

            notifyModified()
        }

    var level: Int = 0
        set(value) {
            field = value.coerceIn(0, DataPackManager.config.maximumLevel)
            notifyModified()
        }

    val xpTarget: Int
        get() {
            val scale = DataPackManager.config.advancementScale
            return scale.base + level * scale.scale
        }

    val deathPenalty: Int
        get() {
            val scale = DataPackManager.config.deathPenaltyScale
            return scale.base + level * scale.scale
        }

    private val bonusHp: Int
        get() {
            val config = DataPackManager.config
            return level * config.hpPerLevel + config.hpOffset
        }

    override fun shouldPersist(wasDeath: Boolean) = !wasDeath || !DataPackManager.config.resetOnDeath

    override fun apply(target: EntityPlayer) {
        if (target.world.isRemote()) return

        val modifier = AttributeModifier(MODIFIER_ID, MODIFIER_NAME, bonusHp.toDouble(), 0)
        modifier.isSaved = false

        val maxHealthAttr = target.getAttribute(SharedMonsterAttributes.MAX_HEALTH)
        maxHealthAttr.removeModifier(modifier.id)
        maxHealthAttr.applyModifier(modifier)

        val maxHealth = target.maxHealth
        if (this.healOnApply || target.health > maxHealth) target.health = maxHealth
    }

    companion object {
        val MODIFIER_ID = UUID.fromString("ff859d30-ec60-418f-a5be-6f3de76a514a")!!
        const val MODIFIER_NAME = LevelUpHp.MOD_ID + ".hp_modifier"
    }
}

val EntityPlayer.playerLevelHandler
    get() = getCapability(PlayerLevel.CAPABILITY)

internal class PlayerLevelStorage : Capability.IStorage<PlayerLevelHandler> {

    override fun writeNBT(
        type: Capability<PlayerLevelHandler>,
        instance: PlayerLevelHandler,
        side: EnumFacing?
    ): INBTBase {
        val tag = NBTTagCompound()
        tag.putInt(LEVEL_KEY, instance.level)
        tag.putInt(XP_KEY, instance.xp)
        return tag
    }

    override fun readNBT(
        type: Capability<PlayerLevelHandler>,
        instance: PlayerLevelHandler,
        side: EnumFacing?,
        nbt: INBTBase
    ) {
        val tag = nbt as NBTTagCompound
        instance.level = tag.getInt(LEVEL_KEY)
        instance.xp = tag.getInt(XP_KEY)
    }

    companion object {
        private const val XP_KEY = "xp"
        private const val LEVEL_KEY = "level"
    }
}
