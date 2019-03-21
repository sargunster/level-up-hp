package me.sargunvohra.leveluphp.core;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.sargunvohra.leveluphp.capability.PlayerLevelHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** This class is responsible for managing progression of the player's hp level. */
@Log4j2
public class ProgressionController {

  @SubscribeEvent
  void onLivingDeath(LivingDeathEvent event) {
    val source = event.getSource().getTrueSource();
    if (!(source instanceof EntityPlayer)) return;
    source.getCapability(PlayerLevelHandler.CAPABILITY).ifPresent(hpData -> {
      if (hpData.addXp(1)) source.sendMessage(new TextComponentString("Level up!"));
    });
  }
}
