package me.sargunvohra.svlib.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 * Variant of SimpleCapabilityProvider for capabilities that want to save data.
 *
 * @param <I> The capability interface
 */
public class SerializableCapabilityProvider<I> extends SimpleCapabilityProvider<I>
    implements ICapabilitySerializable<NBTTagCompound> {

  public SerializableCapabilityProvider(Capability<I> type) {
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
