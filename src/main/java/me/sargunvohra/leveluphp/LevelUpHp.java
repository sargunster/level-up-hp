package me.sargunvohra.leveluphp;

import lombok.extern.log4j.Log4j2;
import me.sargunvohra.leveluphp.command.RootCommand;
import me.sargunvohra.leveluphp.core.PlayerLevelHandler;
import me.sargunvohra.leveluphp.core.ProgressionController;
import me.sargunvohra.leveluphp.gui.XpBarGuiController;
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
    FMLJavaModLoadingContext.get().getModEventBus().register(this);
    MinecraftForge.EVENT_BUS.register(this);

    PlayerLevelHandler.register();
    new ProgressionController().register();
    new XpBarGuiController().register();
  }

  @SubscribeEvent
  void onSetup(FMLCommonSetupEvent event) {
    PlayerLevelHandler.setup();
  }

  @SubscribeEvent
  void onServerStarting(FMLServerStartingEvent event) {
    event.getCommandDispatcher().register(RootCommand.register());
  }
}
