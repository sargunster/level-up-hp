package me.sargunvohra.leveluphp.extensions

import me.sargunvohra.leveluphp.Capabilities.LUHP_DATA
import me.sargunvohra.leveluphp.ExpBarUpdateMessage
import me.sargunvohra.leveluphp.LevelUpHpMod
import me.sargunvohra.leveluphp.LuhpData
import me.sargunvohra.leveluphp.ModConfig
import me.sargunvohra.leveluphp.constants.MOD_ID
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.TextComponentString

fun EntityPlayer.sendStatusMsg(message: String) = sendStatusMessage(TextComponentString(message), true)

fun EntityPlayerMP.luhpInitialize() {
    luhpData.initialized = true
    luhpXp = 0
    luhpLevel = 0
    health = maxHealth
}

fun EntityPlayerMP.luhpUpdateHpModifier() {
    val maxHealthAttr = getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)

    val existingModifier = maxHealthAttr.modifiers.find { it.name == "$MOD_ID.hpmod" }
    if (existingModifier != null)
        maxHealthAttr.removeModifier(existingModifier)

    val amount = ModConfig.hpOffset + ModConfig.hpPerLevel * luhpLevel
    val newModifier = AttributeModifier("$MOD_ID.hpmod", amount.toDouble(), 0)
    maxHealthAttr.applyModifier(newModifier.setSaved(true))

    if (health > 20) {
        health--
        health++
    }
}

val EntityPlayerMP.luhpData: LuhpData
    get() = getCapability(LUHP_DATA, null)!!

var EntityPlayerMP.luhpLevel: Int
    get() = luhpData.level
    set(value) {
        var newLevel = value

        // apply bounds
        if (newLevel > ModConfig.maximumLevel)
            newLevel = ModConfig.maximumLevel
        if (newLevel < 0)
            newLevel = 0

        // update data value
        luhpData.level = newLevel

        // update health bar
        val oldHp = maxHealth
        luhpUpdateHpModifier()
        val newHp = maxHealth

        // update current health value
        if (newHp > oldHp)
            heal(newHp - oldHp)
        if (health > newHp)
            health = newHp

        luhpUpdateClientOverlay()
    }

var EntityPlayerMP.luhpXp: Int
    get() = luhpData.xp
    set(value) {
        luhpData.xp = if (value > 0) value else 0

        while (luhpData.xp >= neededLuhpXp) {
            luhpData.xp -= neededLuhpXp
            luhpLevel++
        }

        luhpUpdateClientOverlay()
    }

val EntityPlayerMP.neededLuhpXp: Int
    get() = ModConfig.neededXpBase + luhpLevel * ModConfig.neededXpScale

val EntityPlayerMP.penaltyLuhpXp: Int
    get() = ModConfig.deathXpPenaltyBase + luhpLevel * ModConfig.deathXpPenaltyScale

fun EntityPlayerMP.luhpUpdateClientOverlay() {
    val message = if (luhpLevel < ModConfig.maximumLevel)
        ExpBarUpdateMessage(luhpXp, neededLuhpXp)
    else
        ExpBarUpdateMessage(-1, -1)
    LevelUpHpMod.NETWORK_WRAPPER.sendTo(message, this)
}