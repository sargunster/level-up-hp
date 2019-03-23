package me.sargunvohra.leveluphp.core

import me.sargunvohra.leveluphp.BuildConfig
import me.sargunvohra.leveluphp.LevelUpHp
import me.sargunvohra.leveluphp.Resources
import me.sargunvohra.svlib.capability.PlayerCapability
import me.sargunvohra.svlib.capability.PlayerCapabilityController
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import java.util.UUID

/** Attach this capability to any player that should have the Level Up HP ability.  */
class PlayerLevelHandler : PlayerCapability() {

    var xp: Int = 0
        set(value) {
            field = value
            while (xp >= xpTarget) {
                field -= xpTarget
                level++
                healOnApply = true
            }
            notifyModified()
        }

    var level: Int = 0
        set(value) {
            field = value
            notifyModified()
        }

    private var healOnApply = false

    val xpTarget get() = 4

    private val numBonusHearts get() = level

    override fun shouldPersist(wasDeath: Boolean) = true

    override fun apply(target: EntityPlayer) {
        if (target.world.isRemote()) return

        val modifier = AttributeModifier(MODIFIER_ID, MODIFIER_NAME, numBonusHearts.toDouble(), 0)
        modifier.isSaved = false

        val maxHealthAttr = target.getAttribute(SharedMonsterAttributes.MAX_HEALTH)
        maxHealthAttr.removeModifier(modifier.id)
        maxHealthAttr.applyModifier(modifier)

        val maxHealth = target.maxHealth
        if (this.healOnApply || target.health > maxHealth) target.health = maxHealth
    }

    companion object {
        @JvmStatic
        @CapabilityInject(PlayerLevelHandler::class)
        lateinit var CAPABILITY: Capability<PlayerLevelHandler>

        val MODIFIER_ID = UUID.fromString("ff859d30-ec60-418f-a5be-6f3de76a514a")!!
        const val MODIFIER_NAME = LevelUpHp.MOD_ID + ".hp_modifier"

        fun register() {
            MinecraftForge.EVENT_BUS.register(
                PlayerCapabilityController(
                    capSupplier = { CAPABILITY },
                    key = Resources.get("player_level_handler"),
                    protocolVersion = BuildConfig.VERSION
                )
            )
        }

        fun setup() {
            CapabilityManager.INSTANCE.register(
                PlayerLevelHandler::class.java,
                PlayerLevelStorage()
            ) { PlayerLevelHandler() }
        }
    }
}

val EntityPlayer.playerLevelHandler
    get() = getCapability(PlayerLevelHandler.CAPABILITY)
