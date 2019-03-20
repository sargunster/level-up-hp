package me.sargunvohra.svlib.capability;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

/**
 * Variant of SimpleCapabilityProvider for capabilities that want to save data. If this is attached
 * to a player, the data will not persist across death or return from The End.
 */
public class SerializableCapabilityProvider<Handler> extends SimpleCapabilityProvider<Handler>
    implements ICapabilitySerializable<NBTTagCompound> {

  public SerializableCapabilityProvider(Capability<Handler> capability) {
    super(capability);
  }

  @Override
  public NBTTagCompound serializeNBT() {
    return (NBTTagCompound)
        getCapability().getStorage().writeNBT(getCapability(), getInstance(), null);
  }

  @Override
  public void deserializeNBT(NBTTagCompound nbt) {
    getCapability().getStorage().readNBT(getCapability(), getInstance(), null, nbt);
  }
}
