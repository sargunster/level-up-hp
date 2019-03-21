package me.sargunvohra.leveluphp.capability;

import lombok.val;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

final class PlayerLevelStorage implements Capability.IStorage<PlayerLevelHandler> {
  private static final String XP_KEY = "xp";
  private static final String LEVEL_KEY = "level";

  @Override
  public INBTBase writeNBT(
      Capability<PlayerLevelHandler> type, PlayerLevelHandler instance, EnumFacing side) {
    val tag = new NBTTagCompound();
    tag.putInt(LEVEL_KEY, instance.getLevel());
    tag.putInt(XP_KEY, instance.getXp());
    return tag;
  }

  @Override
  public void readNBT(
      Capability<PlayerLevelHandler> type,
      PlayerLevelHandler instance,
      EnumFacing side,
      INBTBase nbt) {
    val tag = (NBTTagCompound) nbt;
    instance.setLevel(tag.getInt(LEVEL_KEY));
    instance.setXp(tag.getInt(XP_KEY));
  }
}
