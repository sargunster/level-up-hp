package me.sargunvohra.svlib.capability;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * This class is responsible for attaching PlayerCapabilities to players, and making sure the data
 * is always consistent, such as across dimensional travel or respawn.
 *
 * <p>TODO sync to client
 */
@Log4j2
@RequiredArgsConstructor
public class PlayerCapabilityManager<Handler extends PlayerCapability> {

  private final Supplier<Capability<Handler>> capability;
  private final ResourceLocation key;

  @SubscribeEvent
  void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
    val entity = event.getObject();
    if (!(entity instanceof EntityPlayer)) return;

    val provider = new PlayerCapabilityProvider<Handler>(capability.get(), key);
    event.addCapability(key, provider);
    provider.attach((EntityPlayer) entity);
  }

  @SubscribeEvent
  void onPlayerClone(PlayerEvent.Clone event) {
    event
        .getEntityPlayer()
        .getCapability(capability.get())
        .ifPresent(handler -> initializeClone(event.isWasDeath(), event.getOriginal(), handler));
  }

  private void initializeClone(boolean wasDeath, EntityPlayer oldPlayer, Handler newHandler) {
    if (!wasDeath || newHandler.shouldPersistOnDeath()) {
      val oldData = oldPlayer.getEntityData().get(key.toString());
      capability.get().getStorage().readNBT(capability.get(), newHandler, null, oldData);
    }
  }
}
