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
 * Simple provider for use with capabilities that simply attach without complex logic.
 *
 * @param <I> The capability interface
 */
@Getter
public class SimpleCapabilityProvider<I> implements ICapabilityProvider {

  private final Capability<I> type;
  private final I instance;

  public SimpleCapabilityProvider(Capability<I> type) {
    this.type = type;
    this.instance = type.getDefaultInstance();
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> type, @Nullable EnumFacing side) {
    if (type != this.type) return LazyOptional.empty();

    //noinspection NullableProblems
    val ret = LazyOptional.of(this::getInstance);

    // noinspection unchecked
    return (LazyOptional<T>) ret;
  }
}
