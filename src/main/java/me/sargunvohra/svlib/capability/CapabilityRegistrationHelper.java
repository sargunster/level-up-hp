package me.sargunvohra.svlib.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public abstract class CapabilityRegistrationHelper<T> {

  public abstract Class<T> getJavaClass();

  public T getNewDefaultImpl() {
    return null;
  }

  public Capability.IStorage<T> getDefaultStorage() {
    return null;
  }

  private void register() {
    CapabilityManager.INSTANCE.register(
        getJavaClass(), getDefaultStorage(), this::getNewDefaultImpl);
  }

  public final void registerListeners() {
    FMLJavaModLoadingContext.get()
        .getModEventBus()
        .addListener((FMLCommonSetupEvent event) -> register());
  }
}
