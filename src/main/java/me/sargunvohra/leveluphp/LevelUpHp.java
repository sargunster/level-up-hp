package me.sargunvohra.leveluphp;

import me.sargunvohra.leveluphp.core.ProgressionController;
import me.sargunvohra.leveluphp.data.ILevelHandler;
import me.sargunvohra.leveluphp.data.PlayerLevelManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LevelUpHp.MOD_ID)
public class LevelUpHp {

  static final String MOD_ID = BuildConfig.NAME;
  public static Logger LOGGER = LogManager.getLogger();

  public LevelUpHp() {
    new ILevelHandler.RegistrationHelper().registerListeners();

    MinecraftForge.EVENT_BUS.register(new PlayerLevelManager());
    MinecraftForge.EVENT_BUS.register(new ProgressionController());
  }
}
