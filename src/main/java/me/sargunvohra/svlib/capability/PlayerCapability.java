package me.sargunvohra.svlib.capability;

import lombok.val;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashSet;
import java.util.Set;

public abstract class PlayerCapability {
  private Set<Runnable> listeners = new HashSet<>();

  public void apply(EntityPlayer target) {}

  public boolean shouldPersistOnDeath() {
    return false;
  }

  public final void addListener(Runnable listener) {
    listeners.add(listener);
  }

  public final void notifyModified() {
    for (val listener : listeners) listener.run();
  }
}
