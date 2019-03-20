package me.sargunvohra.leveluphp.capability;

import java.util.UUID;

import lombok.*;
import me.sargunvohra.leveluphp.LevelUpHp;
import me.sargunvohra.leveluphp.Resources;
import me.sargunvohra.svlib.capability.PlayerCapability;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

/** Attach this capability to any player that should have the Level Up HP ability. */
public final class PlayerLevelHandler extends PlayerCapability {

  @CapabilityInject(PlayerLevelHandler.class)
  public static Capability<PlayerLevelHandler> CAPABILITY = null;

  public static final ResourceLocation KEY = Resources.get("player_level_handler");

  private int xp;

  public int getXp() {
    return xp;
  }

  public void setXp(int xp) {
    this.xp = xp;
    this.notifyModified();
  }

  public void addXp(int xp) {
    setXp(getXp() + xp);
  }

  @Override
  public void apply(EntityPlayer target) {
    final UUID MODIFIER_ID = UUID.fromString("ff859d30-ec60-418f-a5be-6f3de76a514a");
    final String MODIFIER_NAME = LevelUpHp.MOD_ID + ".hp_modifier";

    val maxHealthAttr = target.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
    val modifier = new AttributeModifier(MODIFIER_ID, MODIFIER_NAME, getXp(), 0);
    modifier.setSaved(false);
    maxHealthAttr.removeModifier(modifier.getID());
    maxHealthAttr.applyModifier(modifier);
  }

  public static void register() {
    CapabilityManager.INSTANCE.register(
        PlayerLevelHandler.class, new PlayerLevelStorage(), PlayerLevelHandler::new);
  }
}
