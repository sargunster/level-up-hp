package me.sargunvohra.svlib.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

@Getter
public class SimpleCapabilityProvider<I> implements ICapabilityProvider {

  private final Capability<I> type;
  private final I instance;

  private LazyOptional<I> cached = null;

  public SimpleCapabilityProvider(Capability<I> type) {
    this.type = type;
    this.instance = type.getDefaultInstance();
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> type, @Nullable EnumFacing side) {
    if (type != this.type) return LazyOptional.empty();

    // noinspection unchecked
    return (LazyOptional<T>) LazyOptional.of(this::getInstance);
  }
}
