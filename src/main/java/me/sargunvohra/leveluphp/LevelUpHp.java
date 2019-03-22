package me.sargunvohra.leveluphp;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.sargunvohra.leveluphp.capability.PlayerLevelHandler;
import me.sargunvohra.leveluphp.command.RootCommand;
import me.sargunvohra.leveluphp.core.ProgressionController;
import me.sargunvohra.svlib.capability.PlayerCapabilityManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** Core mod class responsible for registering all the components that make the mod do its thing. */
@Log4j2
@Mod(LevelUpHp.MOD_ID)
public class LevelUpHp {

  public static final String MOD_ID = BuildConfig.NAME;

  public LevelUpHp() {
    registerModEventListeners();
    registerForgeEventListeners();
  }

  void registerModEventListeners() {
    FMLJavaModLoadingContext.get().getModEventBus().register(this);
  }

  void registerForgeEventListeners() {
    Object[] eventListeners = {
      this,
      new PlayerCapabilityManager<>(
          () -> PlayerLevelHandler.CAPABILITY,
          PlayerLevelHandler.KEY,
          player -> true,
          BuildConfig.VERSION),
      new ProgressionController()
    };
    for (val listener : eventListeners) MinecraftForge.EVENT_BUS.register(listener);
  }

  @SubscribeEvent
  void onSetup(FMLCommonSetupEvent event) {
    PlayerLevelHandler.register();
  }

  @SubscribeEvent
  void onServerStarting(FMLServerStartingEvent event) {
    event.getCommandDispatcher().register(RootCommand.register());
  }
}
