package me.sargunvohra.leveluphp;

import net.minecraft.util.ResourceLocation;

public final class Resources {
  private Resources() {
    throw new IllegalStateException();
  }

  public static ResourceLocation get(String name) {
    return new ResourceLocation(LevelUpHp.MOD_ID, name);
  }
}
