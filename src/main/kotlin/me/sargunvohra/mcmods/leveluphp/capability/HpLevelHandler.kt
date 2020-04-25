package me.sargunvohra.mcmods.leveluphp.capability

import me.sargunvohra.mcmods.leveluphp.config.LevellingConfigManager
import me.sargunvohra.mcmods.leveluphp.criterion.LuhpCriterionTriggers
import me.sargunvohra.mcmods.leveluphp.network.SyncPacketConsumer
import me.sargunvohra.mcmods.leveluphp.sound.LuhpSoundEvents
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.MobEntity
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT
import net.minecraft.util.SoundCategory
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.registries.RegistryManager
import java.util.*

class HpLevelHandler(
    val player: PlayerEntity
) {
    private var _xp = 0
    private var _level = 0

    private var justLevelledUp = false

    var xp
        get() = _xp
        set(value) {
            _xp = value
            onModified()
        }

    var level
        get() = _level
        set(value) {
            justLevelledUp = justLevelledUp || (!isMaxedOut && value > _level)
            _level = value
            onModified()
        }

    val currentXpTarget get() = config.xpTargetFunction(level)
    val isMaxedOut get() = level >= config.maximumLevel

    val config get() = LevellingConfigManager.config

    fun applyKill(killed: Entity) {
        val typeId = RegistryManager.ACTIVE.getRegistry(EntityType::class.java).getKey(killed.type)
        val gain = config.overrides[typeId.toString()]
            ?: when (killed) {
                is AnimalEntity -> config.primaryXpValues.animal
                is MobEntity -> config.primaryXpValues.mob
                else -> 0
            }
        if (gain != 0) {
            xp += gain
        }
    }

    fun applyDeathPenalty() {
        if (config.resetOnDeath) {
            _xp = 0
            _level = 0
        } else {
            _xp -= config.xpPenaltyFunction(level)
            _level -= config.levelPenaltyFunction(level)
        }
        onModified()
    }

    fun copyFrom(oldHandler: HpLevelHandler) {
        _level = oldHandler.level
        _xp = oldHandler.xp
        onModified()
    }

    fun onModified() {
        // apply constraints
        _level = _level.coerceIn(0, config.maximumLevel)
        _xp = _xp.coerceAtLeast(0)

        // level up if we reached the target
        while (_xp >= currentXpTarget && !isMaxedOut) {
            _xp -= currentXpTarget
            _level++
            justLevelledUp = true
        }

        // lock the xp bar if we can't level up anymore
        if (_level == config.maximumLevel) {
            _xp = 0
        }

        applyToPlayer()
    }

    fun applyToPlayer() {
        // we only act on the server side
        val player = player as? ServerPlayerEntity ?: return

        // sync to client
        if (player.connection != null)
            SyncPacketConsumer.send(player, this)

        // create the hp modifier
        val modifier = AttributeModifier(
            UUID.fromString("ff859d30-ec60-418f-a5be-6f3de76a514a"),
            "Level Up HP modifier",
            (config.hpOffset + level * config.hpPerLevel).toDouble(),
            AttributeModifier.Operation.ADDITION
        )
        modifier.isSaved = false

        // apply the hp modifier to the player, replacing the old one
        val maxHealthAttr = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH)
        maxHealthAttr.removeModifier(modifier)
        maxHealthAttr.applyModifier(modifier)

        // if we just levelled up, play a sound, show a message, and optionally heal
        if (justLevelledUp) {
            justLevelledUp = false

            LuhpCriterionTriggers.LEVEL_UP.test(player)

            player.sendStatusMessage(StringTextComponent("§c§lHP up!"), true)

            player.world.playSound(
                null,
                player.posX, player.posY, player.posZ,
                LuhpSoundEvents.LEVEL_UP,
                SoundCategory.PLAYERS,
                1f, 1f
            )

            if (config.healOnLevelUp) {
                player.health = player.maxHealth
            }
        }

        // if level went down, we need to cap hp
        if (player.health > player.maxHealth) {
            player.health = player.maxHealth
        }
    }

    fun writeToTag(): INBT {
        val ret = CompoundNBT()
        ret.putInt("xp", xp)
        ret.putInt("level", level)
        return ret
    }

    fun readFromTag(tag: INBT) {
        tag as CompoundNBT
        _xp = tag.getInt("xp")
        _level = tag.getInt("level")
        onModified()
    }
}
