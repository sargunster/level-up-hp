package me.sargunvohra.leveluphp;

import lombok.extern.log4j.Log4j2;
import me.sargunvohra.leveluphp.core.ProgressionController;
import me.sargunvohra.leveluphp.data.ILevelHandler;
import me.sargunvohra.leveluphp.data.PlayerLevelManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/** Core mod class responsible for registering all the components that make the mod do its thing. */
@Log4j2
@Mod(LevelUpHp.MOD_ID)
public class LevelUpHp {

  public static final String MOD_ID = BuildConfig.NAME;

  public LevelUpHp() {
    LOG.debug("register event bus listeners");
    FMLJavaModLoadingContext.get().getModEventBus().register(this);
    MinecraftForge.EVENT_BUS.register(new PlayerLevelManager());
    MinecraftForge.EVENT_BUS.register(new ProgressionController());
  }

  @SubscribeEvent
  void onSetup(FMLCommonSetupEvent event) {
    LOG.debug("register capabilities");
    ILevelHandler.register();
  }
}
