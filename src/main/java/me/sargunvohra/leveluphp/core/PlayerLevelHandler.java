package me.sargunvohra.leveluphp.core;

import java.util.UUID;
import lombok.*;
import me.sargunvohra.leveluphp.BuildConfig;
import me.sargunvohra.leveluphp.LevelUpHp;
import me.sargunvohra.leveluphp.Resources;
import me.sargunvohra.svlib.capability.PlayerCapability;
import me.sargunvohra.svlib.capability.PlayerCapabilityController;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

/** Attach this capability to any player that should have the Level Up HP ability. */
@ToString
public final class PlayerLevelHandler extends PlayerCapability {

  @CapabilityInject(PlayerLevelHandler.class)
  public static Capability<PlayerLevelHandler> CAPABILITY = null;

  public static final ResourceLocation KEY = Resources.get("player_level_handler");

  @Getter private int xp;
  @Getter private int level;

  @ToString.Exclude private boolean healOnApply;

  public boolean setXp(int xp) {
    this.xp = xp;
    var levelUp = false;
    while (this.xp >= xpTarget()) {
      levelUp = true;
      this.xp -= xpTarget();
      this.level++;
    }
    this.healOnApply = true;
    this.notifyModified();
    return levelUp;
  }

  public void setLevel(int level) {
    this.level = level;
    this.notifyModified();
  }

  public boolean addXp(int xp) {
    return setXp(getXp() + xp);
  }

  public void addLevel(int levels) {
    setLevel(getLevel() + levels);
  }

  public int numBonusHearts() {
    return getLevel();
  }

  public int xpTarget() {
    return 4;
  }

  public float xpFraction() {
    return (float) getXp() / xpTarget();
  }

  @Override
  public boolean shouldPersist(boolean wasDeath) {
    return true;
  }

  @Override
  public void apply(EntityPlayer target) {
    if (target.world.isRemote()) return;

    final UUID MODIFIER_ID = UUID.fromString("ff859d30-ec60-418f-a5be-6f3de76a514a");
    final String MODIFIER_NAME = LevelUpHp.MOD_ID + ".hp_modifier";

    val modifier = new AttributeModifier(MODIFIER_ID, MODIFIER_NAME, numBonusHearts(), 0);
    modifier.setSaved(false);

    val maxHealthAttr = target.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
    maxHealthAttr.removeModifier(modifier.getID());
    maxHealthAttr.applyModifier(modifier);

    val maxHealth = target.getMaxHealth();
    if (this.healOnApply || target.getHealth() > maxHealth) target.setHealth(maxHealth);
  }

  public static void register() {
    MinecraftForge.EVENT_BUS.register(
        new PlayerCapabilityController<>(
            () -> PlayerLevelHandler.CAPABILITY,
            PlayerLevelHandler.KEY,
            player -> true,
            BuildConfig.VERSION));
  }

  public static void setup() {
    CapabilityManager.INSTANCE.register(
        PlayerLevelHandler.class, new PlayerLevelStorage(), PlayerLevelHandler::new);
  }
}
