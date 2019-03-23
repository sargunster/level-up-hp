package me.sargunvohra.leveluphp.core

import net.minecraft.nbt.INBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

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
