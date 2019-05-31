package me.sargunvohra.mcmods.leveluphp.level

import me.sargunvohra.mcmods.leveluphp.LevelUpHp
import me.sargunvohra.mcmods.leveluphp.advancement.LevelUpCriterion
import me.sargunvohra.mcmods.leveluphp.network.SyncPacketConsumer
import net.minecraft.entity.Entity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.mob.Monster
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.network.chat.TextComponent
import net.minecraft.util.registry.Registry
import java.util.UUID

class HpLevelHandler {
    var player: PlayerEntity? = null
        set(value) {
            if (value != null) {
                field = value
                applyToPlayer()
            }
        }

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

    val config get() = LevelUpHp.reloadListener.config

    fun applyKill(killed: Entity) {
        val typeId = Registry.ENTITY_TYPE.getId(killed.type)
        val gain = config.overrides[typeId.toString()]
            ?: when (killed) {
                is AnimalEntity -> config.primaryXpValues.animal
                is Monster -> config.primaryXpValues.mob
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

    private fun applyToPlayer() {
        // we only act on the server side
        val player = player as? ServerPlayerEntity ?: return

        // sync to client
        if (player.networkHandler != null)
            SyncPacketConsumer.send(player, this)

        // create the hp modifier
        val modifier = EntityAttributeModifier(
            UUID.fromString("ff859d30-ec60-418f-a5be-6f3de76a514a"),
            "Level Up HP modifier",
            (config.hpOffset + level * config.hpPerLevel).toDouble(),
            EntityAttributeModifier.Operation.ADDITION
        )
        modifier.setSerialize(false)

        // apply the hp modifier to the player, replacing the old one
        val maxHealthAttr = player.getAttributeInstance(EntityAttributes.MAX_HEALTH)
        maxHealthAttr.removeModifier(modifier.id)
        maxHealthAttr.addModifier(modifier)

        // if we just levelled up, play a sound, show a message, and optionally heal
        if (justLevelledUp) {
            justLevelledUp = false

            LevelUpCriterion.handle(player)

            player.addChatMessage(TextComponent("§c§lHP up!"), true)

            player.world.playSound(
                null,
                player.x, player.y, player.z,
                LevelUpHp.levelUpSound,
                SoundCategory.PLAYERS,
                1f, 1f
            )

            if (config.healOnLevelUp) {
                player.health = player.healthMaximum
            }
        }

        // if level went down, we need to cap hp
        if (player.health > player.healthMaximum) {
            player.health = player.healthMaximum
        }
    }

    fun writeToTag(): Tag {
        val ret = CompoundTag()
        ret.putInt("xp", xp)
        ret.putInt("level", level)
        return ret
    }

    fun readFromTag(tag: Tag) {
        tag as CompoundTag
        _xp = tag.getInt("xp")
        _level = tag.getInt("level")
        onModified()
    }
}
