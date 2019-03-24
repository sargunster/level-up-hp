package me.sargunvohra.leveluphp.level

import me.sargunvohra.leveluphp.LevelUpHp
import me.sargunvohra.leveluphp.data.DataPackEventListener
import me.sargunvohra.leveluphp.init.SoundEvents
import me.sargunvohra.svlib.capability.PlayerCapability
import net.minecraft.entity.Entity
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.monster.IMob
import net.minecraft.entity.passive.IAnimal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.SoundCategory
import net.minecraft.util.text.TextComponentTranslation
import java.util.UUID

class Leveller : PlayerCapability() {
    private var justLevelledUp = false

    private var _xp: Int = 0
    var xp: Int
        set(value) {
            _xp = value
            reconfigure()
        }
        get() = _xp

    private var _level: Int = 0
    var level: Int
        set(value) {
            justLevelledUp = justLevelledUp || (!maxedOut && value > _level)
            _level = value
            reconfigure()
        }
        get() = _level

    fun restore(level: Int, xp: Int) {
        _level = level
        _xp = xp
        reconfigure()
    }

    val config
        get() = DataPackEventListener.config

    fun reconfigure() {
        // apply constraints
        _level = _level.coerceIn(0, config.maximumLevel)
        _xp = _xp.coerceAtLeast(0)

        // level up if we reached the target
        while (_xp >= xpTarget && !maxedOut) {
            _xp -= xpTarget
            _level++
            justLevelledUp = true
        }

        // lock the xp bar if we can't grow anymore
        if (_level == config.maximumLevel) {
            _xp = 0
        }

        notifyModified()
    }

    val maxedOut
        get() = level >= config.maximumLevel

    val xpTarget
        get() = config.xpTargetFunction(level).coerceAtLeast(1)

    val xpPenalty
        get() = config.xpPenaltyFunction(level)

    val levelPenalty
        get() = config.levelPenaltyFunction(level)

    private val bonusHp
        get() = level * config.hpPerLevel + config.hpOffset

    fun onKill(killed: Entity) {
        val gain = config.overrides[killed.type.registryName.toString()]
            ?: when (killed) {
                is IAnimal -> config.primaryXpValues.animal
                is IMob -> config.primaryXpValues.mob
                else -> 0
            }
        if (gain != 0) {
            xp += gain
        }
    }

    override fun shouldPersist(wasDeath: Boolean) =
        !wasDeath || !DataPackEventListener.config.resetOnDeath

    override fun apply(owner: EntityPlayer) {
        if (owner.world.isRemote()) return

        val modifier =
            AttributeModifier(MODIFIER_ID, MODIFIER_NAME, bonusHp.toDouble(), 0)
        modifier.isSaved = false

        val maxHealthAttr = owner.getAttribute(SharedMonsterAttributes.MAX_HEALTH)
        maxHealthAttr.removeModifier(modifier.id)
        maxHealthAttr.applyModifier(modifier)

        val maxHealth = owner.maxHealth

        if (this.justLevelledUp) {
            justLevelledUp = false

            owner.sendStatusMessage(TextComponentTranslation("status.leveluphp.levelup"), true)

            owner.world.playSound(
                null,
                owner.posX, owner.posY, owner.posZ,
                SoundEvents.levelUp,
                SoundCategory.PLAYERS,
                1f, 1f
            )

            if (config.healOnLevelUp) {
                owner.health = maxHealth
            }
        }

        if (owner.health > maxHealth) {
            owner.health = maxHealth
        }
    }

    companion object {
        val MODIFIER_ID = UUID.fromString("ff859d30-ec60-418f-a5be-6f3de76a514a")!!
        const val MODIFIER_NAME = LevelUpHp.MOD_ID + ".hp_modifier"
    }
}
