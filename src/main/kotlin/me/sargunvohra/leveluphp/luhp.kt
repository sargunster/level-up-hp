package me.sargunvohra.leveluphp

import me.sargunvohra.leveluphp.LuhpCapabilities.LUHP_DATA
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayerMP

class HpModifier(amount: Int) : AttributeModifier(HpModifier.name, amount.toDouble(), 0) {

    init {
        isSaved = true
    }

    companion object {
        const val name = "$MOD_ID.hpmod"
    }
}

fun EntityPlayerMP.luhpSync() {
    val attr = getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
    val mod = attr.modifiers.find { it.name == HpModifier.name }
    if (mod != null)
        attr.removeModifier(mod)
    val amount = LuhpConfig.hpOffset + LuhpConfig.hpPerLevel * luhpLevel
    attr.applyModifier(HpModifier(amount))

    if (health > 20) {
        health --
        health ++
    }
}

var EntityPlayerMP.luhpLevel: Int
    get() = luhpData.level
    set(value) {
        var newLevel = value

        // apply bounds
        if (newLevel > LuhpConfig.maximumLevel)
            newLevel = LuhpConfig.maximumLevel
        if (newLevel < 0)
            newLevel = 0

        // update data value
        luhpData.level = newLevel

        // update health bar
        val oldHp = maxHealth
        luhpSync()
        val newHp = maxHealth

        // update current health value
        if (newHp > oldHp)
            heal(newHp - oldHp)
        if (health > newHp)
            health = newHp
    }

val EntityPlayerMP.luhpData: LuhpData
    get() = getCapability(LUHP_DATA, null)!!

val EntityPlayerMP.neededLuhpXp: Int
    get() = LuhpConfig.neededXpBase + luhpLevel * LuhpConfig.neededXpScale

val EntityPlayerMP.penaltyLuhpXp: Int
    get() = LuhpConfig.deathXpPenaltyBase + luhpLevel * LuhpConfig.deathXpPenaltyScale

var EntityPlayerMP.luhpXp: Int
    get() = luhpData.xp
    set(value) {
        luhpData.xp = if (value > 0) value else 0
        while (luhpData.xp >= neededLuhpXp) {
            luhpData.xp -= neededLuhpXp
            luhpLevel++
        }
    }