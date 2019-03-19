package me.sargunvohra.leveluphp;

import lombok.experimental.UtilityClass;
import me.sargunvohra.leveluphp.data.ILevelHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

@UtilityClass
public class Capabilities {
  @CapabilityInject(ILevelHandler.class)
  public static Capability<ILevelHandler> LEVEL_HANDLER = null;
}
