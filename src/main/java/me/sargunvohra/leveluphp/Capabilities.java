package me.sargunvohra.leveluphp;

import me.sargunvohra.leveluphp.data.ILevelHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public final class Capabilities {
  private Capabilities() {
    throw new IllegalStateException();
  }

  @CapabilityInject(ILevelHandler.class)
  public static Capability<ILevelHandler> LEVEL_HANDLER = null;
}
