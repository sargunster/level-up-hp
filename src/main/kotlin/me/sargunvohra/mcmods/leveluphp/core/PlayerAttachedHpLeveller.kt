package me.sargunvohra.mcmods.leveluphp.core

import me.sargunvohra.mcmods.leveluphp.LuhpSoundEvents
import me.sargunvohra.mcmods.leveluphp.LuhpCriterionTriggers
import me.sargunvohra.mcmods.leveluphp.config.LevellingConfigManager
import me.sargunvohra.mcmods.leveluphp.core.CapabilityRegistrationSubscriber.HP_LEVELLER_CAPABILITY
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.MobEntity
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraft.util.SoundCategory
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.registries.RegistryManager
import java.util.*

class PlayerAttachedHpLeveller : HpLeveller, ICapabilitySerializable<INBT> {

    private var player: PlayerEntity? = null

    private var justLevelledUp = false

    private var _level = 0
    private var _xp = 0

    private val config get() = LevellingConfigManager.config

    override var level: Int
        get() = _level
        set(value) {
            justLevelledUp = justLevelledUp || (!isMaxedOut && value > level)
            _level = value
            updateState()
        }

    override var xp: Int
        get() = _xp
        set(value) {
            _xp = value
            updateState()
        }

    override fun restoreTo(level: Int, xp: Int) {
        _level = level
        _xp = xp
        updateState()
    }

    override fun handleKillEnemy(enemy: Entity) {
        val typeId = RegistryManager.ACTIVE.getRegistry(EntityType::class.java).getKey(enemy.type)
        val gain = config.overrides[typeId.toString()]
            ?: when (enemy) {
                is AnimalEntity -> config.primaryXpValues.animal
                is MobEntity -> config.primaryXpValues.mob
                else -> 0
            }
        if (gain != 0) {
            xp += gain
        }
    }

    override fun handleDeath() {
        if (config.resetOnDeath) {
            _level = 0
            _xp = 0
        } else {
            _level -= config.levelPenaltyFunction(level)
            _xp -= config.xpPenaltyFunction(level)
        }
        updateState()
    }

    override fun updateState() {
        constrainXpAndLevel()
        levelUpIfAble()
        applyToPlayer()
    }

    private fun constrainXpAndLevel() {
        _level = _level.coerceIn(0, config.maximumLevel)
        _xp = _xp.coerceAtLeast(0)
    }

    private fun levelUpIfAble() {
        while (_xp >= currentXpTarget && !isMaxedOut) {
            _xp -= currentXpTarget
            _level++
            justLevelledUp = true
        }

        if (_level == config.maximumLevel) {
            _xp = 0
        }
    }

    private fun applyToPlayer() {
        // we only act on the server side
        val player = player as? ServerPlayerEntity ?: return

        // sync to client
        if (player.connection != null)
            HpLevellerSyncMessage.create(this).send(player)

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

    fun attachTo(target: PlayerEntity) = this.apply {
        player = target
    }

    override fun <T> getCapability(requested: Capability<T>, side: Direction?): LazyOptional<T> {
        return if (player != null)
            HP_LEVELLER_CAPABILITY.orEmpty<T>(requested, LazyOptional.of { this }).cast()
        else LazyOptional.empty()
    }

    override fun deserializeNBT(nbt: INBT?) {
        LevellingConfigManager.ensureDataLoaded()
        HP_LEVELLER_CAPABILITY.readNBT(this, null, nbt)
    }

    override fun serializeNBT(): INBT? {
        LevellingConfigManager.ensureDataLoaded()
        return HP_LEVELLER_CAPABILITY.writeNBT(this, null)
    }
}
