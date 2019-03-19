package me.sargunvohra.leveluphp.data;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.sargunvohra.leveluphp.Capabilities;
import me.sargunvohra.leveluphp.Resources;
import me.sargunvohra.svlib.capability.SerializableCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * This class is responsible for attaching level handlers to players, and making sure the data is
 * always consistent, such as across player clone events or between the logical server/client.
 */
@Log4j2
public class PlayerLevelManager {

  @SubscribeEvent
  void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
    val entity = event.getObject();
    if (!(entity instanceof EntityPlayer)) return;

    event.addCapability(
        Resources.get("level_handler"),
        new SerializableCapabilityProvider<>(Capabilities.LEVEL_HANDLER));

    LOG.debug("Attached level handler to player {}", entity);
  }

  @SubscribeEvent
  void onPlayerClone(PlayerEvent.Clone event) {
    val original = event.getOriginal();
    val clone = event.getEntityPlayer();

    LOG.debug("onPlayerClone {}, {}", original, clone);

    clone
        .getCapability(Capabilities.LEVEL_HANDLER)
        .ifPresent(
            c -> {
              LOG.debug("capability present in clone");
              original
                  .getCapability(Capabilities.LEVEL_HANDLER)
                  .ifPresent(
                      o -> {
                        LOG.debug("capability present in original");
                        c.copyFrom(o);
                        LOG.debug("onPlayerClone original: {}, clone: {}", o, c);
                      });
            });
  }
}
