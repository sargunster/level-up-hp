package me.sargunvohra.mcmods.leveluphp.core

import net.minecraft.entity.Entity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability

interface HpLeveller {
    var level: Int
    var xp: Int
    fun restoreTo(level: Int, xp: Int)
    fun handleKillEnemy(enemy: Entity)
    fun handleDeath()
    fun updateState()

    object Serializer : Capability.IStorage<HpLeveller> {
        override fun readNBT(
            capability: Capability<HpLeveller>,
            instance: HpLeveller,
            side: Direction?,
            nbt: INBT
        ) {
            require(nbt is CompoundNBT)
            instance.restoreTo(level = nbt.getInt("level"), xp = nbt.getInt("xp"))
        }

        override fun writeNBT(
            capability: Capability<HpLeveller>,
            instance: HpLeveller,
            side: Direction?
        ): CompoundNBT {
            return CompoundNBT().apply {
                putInt("level", instance.level)
                putInt("xp", instance.xp)
            }
        }
    }
}
