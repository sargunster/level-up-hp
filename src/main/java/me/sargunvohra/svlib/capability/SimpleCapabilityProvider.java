package me.sargunvohra.svlib.capability;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleCapabilityProvider<I> implements ICapabilityProvider {

  final Capability<I> capability;
  final I instance;

  public SimpleCapabilityProvider(Capability<I> capability) {
    this.capability = capability;
    this.instance = capability.getDefaultInstance();
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(
      @Nonnull Capability<T> capType, @Nullable EnumFacing side) {
    // noinspection unchecked
    return capType == capability
        ? (LazyOptional<T>) LazyOptional.of(() -> this.instance)
        : LazyOptional.empty();
  }
}
