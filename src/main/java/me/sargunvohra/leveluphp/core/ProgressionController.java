package me.sargunvohra.leveluphp.core;

import me.sargunvohra.leveluphp.Capabilities;
import me.sargunvohra.leveluphp.LevelUpHp;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ProgressionController {

  @SubscribeEvent
  void onLivingDeath(LivingDeathEvent event) {
    LevelUpHp.LOGGER.info("On living death: {}", event);
    Entity source = event.getSource().getTrueSource();
    if (source == null) return;

    source
      .getCapability(Capabilities.LEVEL_HANDLER)
      .ifPresent(hpData -> LevelUpHp.LOGGER.info("New XP: {}", hpData.addXp(1)));
  }
}
