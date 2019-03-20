package me.sargunvohra.leveluphp.data;

import java.util.UUID;
import lombok.Data;
import lombok.val;
import me.sargunvohra.leveluphp.LevelUpHp;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Attach this capability to any entity that should gain hp levels by killing mobs (namely, players)
 */
public interface ILevelHandler {

  int getXp();

  void setXp(int xp);

  default void addXp(int xp) {
    setXp(getXp() + xp);
  }

  default int getLevel() {
    return getXp();
  }

  default double getBonusHearts() {
    return getLevel();
  }

  default void applyModifier(ICapabilityProvider target) {
    final UUID MODIFIER_ID = UUID.fromString("ff859d30-ec60-418f-a5be-6f3de76a514a");
    final String MODIFIER_NAME = LevelUpHp.MOD_ID + ".hp_modifier";

    if (!(target instanceof EntityLivingBase)) throw new UnsupportedOperationException();
    val living = (EntityLivingBase) target;
    val maxHealthAttr = living.getAttribute(SharedMonsterAttributes.MAX_HEALTH);

    val modifier = new AttributeModifier(MODIFIER_ID, MODIFIER_NAME, getBonusHearts(), 0);
    maxHealthAttr.removeModifier(modifier.getID());
    maxHealthAttr.applyModifier(modifier);
  }

  @Data
  final class DefaultImpl implements ILevelHandler {
    private int xp;
  }

  final class DefaultStorage implements Capability.IStorage<ILevelHandler> {
    private static final String XP_KEY = "xp";

    @Override
    public INBTBase writeNBT(
        Capability<ILevelHandler> type, ILevelHandler instance, EnumFacing side) {
      val tag = new NBTTagCompound();
      tag.putInt(XP_KEY, instance.getXp());
      return tag;
    }

    @Override
    public void readNBT(
        Capability<ILevelHandler> type, ILevelHandler instance, EnumFacing side, INBTBase nbt) {
      val tag = (NBTTagCompound) nbt;
      instance.setXp(tag.getInt(XP_KEY));
    }
  }

  static void register() {
    CapabilityManager.INSTANCE.register(
        ILevelHandler.class, new DefaultStorage(), DefaultImpl::new);
  }
}
