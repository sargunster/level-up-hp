package me.sargunvohra.svlib.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class SerializableCapabilityProvider<C> extends SimpleCapabilityProvider<C>
    implements ICapabilitySerializable<NBTTagCompound> {

  public SerializableCapabilityProvider(Capability<C> type) {
    super(type);
  }

  @Override
  public NBTTagCompound serializeNBT() {
    return (NBTTagCompound) getType().getStorage().writeNBT(getType(), getInstance(), null);
  }

  @Override
  public void deserializeNBT(NBTTagCompound nbt) {
    getType().getStorage().readNBT(getType(), getInstance(), null, nbt);
  }
}
