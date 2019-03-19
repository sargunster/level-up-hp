package me.sargunvohra.leveluphp;

import me.sargunvohra.leveluphp.core.ProgressionController;
import me.sargunvohra.leveluphp.data.ILevelHandler;
import me.sargunvohra.leveluphp.data.PlayerLevelManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(LevelUpHp.MOD_ID)
public class LevelUpHp {

  public static final String MOD_ID = BuildConfig.NAME;

  public LevelUpHp() {
    new ILevelHandler.RegistrationHelper().registerListeners();

    MinecraftForge.EVENT_BUS.register(new PlayerLevelManager());
    MinecraftForge.EVENT_BUS.register(new ProgressionController());
  }
}
