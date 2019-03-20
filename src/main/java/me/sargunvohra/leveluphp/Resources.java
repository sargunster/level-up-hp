package me.sargunvohra.leveluphp;

import lombok.experimental.UtilityClass;
import net.minecraft.util.ResourceLocation;

/** Utility class to generate ResourceLocations for this mod. */
@UtilityClass
public class Resources {
  public ResourceLocation get(String name) {
    return new ResourceLocation(LevelUpHp.MOD_ID, name);
  }
}
