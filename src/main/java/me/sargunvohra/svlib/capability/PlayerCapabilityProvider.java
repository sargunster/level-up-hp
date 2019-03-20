package me.sargunvohra.svlib.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.val;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Specialized provider for capabilities that attach to players. Supports persisting data across
 * death or return from end.
 */
public class PlayerCapabilityProvider<Handler extends PlayerCapability>
    extends SerializableCapabilityProvider<Handler> {

  private final ResourceLocation key;

  @Nullable private EntityPlayer target;

  public PlayerCapabilityProvider(Capability<Handler> capability, ResourceLocation key) {
    super(capability);
    this.key = key;

    val instance = getInstance();
    instance.addListener(this::writeToEntityData);
    instance.addListener(() -> instance.apply(target));
  }

  public void attach(EntityPlayer target) {
    this.target = target;
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(
      @Nonnull Capability<T> capability, @Nullable EnumFacing side) {
    val ret = super.getCapability(capability, side);
    ret.addListener(it -> this.target = null);
    return ret;
  }

  private void writeToEntityData() {
    if (target == null) return;

    val data = getCapability().getStorage().writeNBT(getCapability(), getInstance(), null);
    if (data == null) return;

    val entityData = target.getEntityData();
    if (!entityData.contains(EntityPlayer.PERSISTED_NBT_TAG))
      entityData.put(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());

    val persistedData = (NBTTagCompound) entityData.get(EntityPlayer.PERSISTED_NBT_TAG);
    persistedData.put(key.toString(), data);
  }
}
