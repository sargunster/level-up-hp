package me.sargunvohra.svlib.capability;

import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * This class is responsible for attaching PlayerCapabilities to players, and making sure the data
 * is always consistent, such as across dimensional travel or respawn.
 */
@Log4j2
@AllArgsConstructor
public class PlayerCapabilityManager<Handler extends PlayerCapability> {

  private final Supplier<Capability<Handler>> capability;
  private final ResourceLocation key;
  private final Predicate<EntityPlayer> filter;

  public PlayerCapabilityManager(Supplier<Capability<Handler>> capability, ResourceLocation key) {
    this(capability, key, player -> true);
  }

  @SubscribeEvent
  void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
    val entity = event.getObject();
    if (!(entity instanceof EntityPlayer)) return;

    val player = (EntityPlayer) entity;
    if (!filter.test(player)) return;

    val provider = new PlayerCapabilityProvider<Handler>(capability.get(), key);
    event.addCapability(key, provider);
    provider.attach(player);
  }

  @SubscribeEvent
  void onPlayerClone(PlayerEvent.Clone event) {
    event
        .getEntityPlayer()
        .getCapability(capability.get())
        .ifPresent(
            handler -> initializeClone(event.isWasDeath(), event.getEntityPlayer(), handler));
  }

  private void initializeClone(boolean wasDeath, EntityPlayer newPlayer, Handler newHandler) {
    if (newHandler.shouldPersist(wasDeath)) {
      val entityData = newPlayer.getEntityData();
      if (!entityData.contains(EntityPlayer.PERSISTED_NBT_TAG)) return;

      val persistedData = (NBTTagCompound) entityData.get(EntityPlayer.PERSISTED_NBT_TAG);
      val keyStr = key.toString();
      if (!persistedData.contains(keyStr)) return;

      val data = persistedData.get(keyStr);
      val cap = capability.get();
      cap.getStorage().readNBT(cap, newHandler, null, data);
    }
  }
}
