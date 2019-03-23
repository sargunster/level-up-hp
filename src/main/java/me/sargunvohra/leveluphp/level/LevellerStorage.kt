package me.sargunvohra.leveluphp.level

import net.minecraft.nbt.INBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

class LevellerStorage :
    Capability.IStorage<Leveller> {

    override fun writeNBT(
        type: Capability<Leveller>,
        instance: Leveller,
        side: EnumFacing?
    ): INBTBase {
        val tag = NBTTagCompound()
        tag.putInt(LEVEL_KEY, instance.level)
        tag.putInt(XP_KEY, instance.xp)
        return tag
    }

    override fun readNBT(
        type: Capability<Leveller>,
        instance: Leveller,
        side: EnumFacing?,
        nbt: INBTBase
    ) {
        val tag = nbt as NBTTagCompound
        instance.restore(level = tag.getInt(LEVEL_KEY), xp = tag.getInt(XP_KEY))
    }

    companion object {
        private const val XP_KEY = "xp"
        private const val LEVEL_KEY = "level"
    }
}
