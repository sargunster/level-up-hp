package me.sargunvohra.svlib.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.val;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Simple provider for use with capabilities that simply attach without complex logic or
 * serialization.
 */
@Getter
public class SimpleCapabilityProvider<Handler> implements ICapabilityProvider {

  private final Capability<Handler> capability;
  private final Handler instance;

  public SimpleCapabilityProvider(Capability<Handler> capability) {
    this.capability = capability;
    this.instance = capability.getDefaultInstance();
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(
      @Nonnull Capability<T> capability, @Nullable EnumFacing side) {
    if (capability != this.capability) return LazyOptional.empty();
    //noinspection NullableProblems
    val ret = LazyOptional.of(this::getInstance);
    // noinspection unchecked
    return (LazyOptional<T>) ret;
  }
}
