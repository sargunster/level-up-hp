package me.sargunvohra.svlib.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class SerializableCapabilityProvider<C> extends SimpleCapabilityProvider<C>
    implements ICapabilitySerializable<NBTTagCompound> {

  public SerializableCapabilityProvider(Capability<C> capability) {
    super(capability);
  }

  @Override
  public NBTTagCompound serializeNBT() {
    return (NBTTagCompound) capability.getStorage().writeNBT(capability, instance, null);
  }

  @Override
  public void deserializeNBT(NBTTagCompound nbt) {
    capability.getStorage().readNBT(capability, instance, null, nbt);
  }
}
