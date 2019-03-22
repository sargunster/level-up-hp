package me.sargunvohra.svlib.capability;

import java.util.function.BiConsumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.val;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
  private final BiConsumer<EntityPlayerMP, Handler> syncToClient;

  @Nullable private EntityPlayer targetPlayer;

  public PlayerCapabilityProvider(
      Capability<Handler> capability,
      ResourceLocation key,
      BiConsumer<EntityPlayerMP, Handler> syncToClient) {
    super(capability);
    this.key = key;
    this.syncToClient = syncToClient;

    val instance = getInstance();
    instance.addListener(this::persistToEntityData);
    instance.addListener(this::syncToClientIfMP);
    instance.addListener(() -> instance.apply(targetPlayer));
  }

  public void attach(EntityPlayer target) {
    this.targetPlayer = target;
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(
      @Nonnull Capability<T> capability, @Nullable EnumFacing side) {
    val ret = super.getCapability(capability, side);
    ret.addListener(it -> this.targetPlayer = null);
    return ret;
  }

  private void persistToEntityData() {
    if (targetPlayer == null) return;

    val data = getCapability().getStorage().writeNBT(getCapability(), getInstance(), null);
    if (data == null) return;

    val entityData = targetPlayer.getEntityData();
    if (!entityData.contains(EntityPlayer.PERSISTED_NBT_TAG))
      entityData.put(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());

    val persistedData = (NBTTagCompound) entityData.get(EntityPlayer.PERSISTED_NBT_TAG);
    persistedData.put(key.toString(), data);
  }

  private void syncToClientIfMP() {
    if (targetPlayer instanceof EntityPlayerMP)
      syncToClient.accept((EntityPlayerMP) this.targetPlayer, getInstance());
  }
}
