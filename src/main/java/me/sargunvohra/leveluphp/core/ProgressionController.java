package me.sargunvohra.leveluphp.core;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.sargunvohra.leveluphp.Capabilities;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Log4j2
public class ProgressionController {

  @SubscribeEvent
  void onLivingDeath(LivingDeathEvent event) {
    LOG.info("On living death: {}", event);
    val source = event.getSource().getTrueSource();
    if (source == null) return;

    source
        .getCapability(Capabilities.LEVEL_HANDLER)
        .ifPresent(
            hpData -> {
              hpData.addXp(1);
              hpData.applyModifier(source);
              LOG.info("New XP: {}", hpData.getXp());
            });
  }
}
