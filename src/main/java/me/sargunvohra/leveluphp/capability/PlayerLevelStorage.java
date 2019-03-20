package me.sargunvohra.leveluphp.capability;

import lombok.val;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

final class PlayerLevelStorage implements Capability.IStorage<PlayerLevelHandler> {
  private static final String XP_KEY = "xp";

  @Override
  public INBTBase writeNBT(Capability<PlayerLevelHandler> type, PlayerLevelHandler instance, EnumFacing side) {
    val tag = new NBTTagCompound();
    tag.putInt(XP_KEY, instance.getXp());
    return tag;
  }

  @Override
  public void readNBT(
    Capability<PlayerLevelHandler> type, PlayerLevelHandler instance, EnumFacing side, INBTBase nbt) {
    val tag = (NBTTagCompound) nbt;
    instance.setXp(tag.getInt(XP_KEY));
  }
}
