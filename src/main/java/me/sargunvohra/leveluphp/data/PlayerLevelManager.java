package me.sargunvohra.leveluphp.data;

import me.sargunvohra.leveluphp.Capabilities;
import me.sargunvohra.leveluphp.LevelUpHp;
import me.sargunvohra.leveluphp.Resources;
import me.sargunvohra.svlib.capability.SerializableCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * This class is responsible for attaching level handlers to players, and making sure the data is
 * always consistent, such as across player clone events or between the logical server/client.
 */
public class PlayerLevelManager {

  @SubscribeEvent
  void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
    Entity entity = event.getObject();
    if (!(entity instanceof EntityPlayer)) return;

    event.addCapability(
        Resources.get("level_handler"),
        new SerializableCapabilityProvider<>(Capabilities.LEVEL_HANDLER));

    LevelUpHp.LOGGER.debug(
        "Attached level handler to player {}", entity);
  }
}
