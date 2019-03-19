package me.sargunvohra.leveluphp.data;

import java.util.UUID;
import lombok.Data;
import lombok.val;
import me.sargunvohra.leveluphp.LevelUpHp;
import me.sargunvohra.svlib.capability.CapabilityRegistrationHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Attach this capability to any entity that should gain hp levels by killing mobs (namely, Players)
 */
public interface ILevelHandler {

  int getXp();

  void setXp(int xp);

  default void addXp(int xp) {
    setXp(getXp() + xp);
  }

  default void copyFrom(ILevelHandler other) {
    setXp(other.getXp());
  }

  default void applyModifier(ICapabilityProvider target) {
    final UUID MODIFIER_ID = UUID.fromString("ff859d30-ec60-418f-a5be-6f3de76a514a");

    if (!(target instanceof EntityLivingBase)) throw new UnsupportedOperationException();
    val living = (EntityLivingBase) target;
    val maxHealthAttr = living.getAttribute(SharedMonsterAttributes.MAX_HEALTH);

    val modifier = new AttributeModifier(MODIFIER_ID, LevelUpHp.MOD_ID + ".hpmod", getXp(), 0);
    modifier.setSaved(true);

    maxHealthAttr.removeModifier(modifier.getID());
    maxHealthAttr.applyModifier(modifier);
  }

  @Data
  final class DefaultImpl implements ILevelHandler {
    private int xp;
  }

  final class DefaultStorage implements Capability.IStorage<ILevelHandler> {
    @Override
    public INBTBase writeNBT(
        Capability<ILevelHandler> type, ILevelHandler instance, EnumFacing side) {
      val tag = new NBTTagCompound();
      tag.putInt("xp", instance.getXp());
      return tag;
    }

    @Override
    public void readNBT(
        Capability<ILevelHandler> type, ILevelHandler instance, EnumFacing side, INBTBase nbt) {
      val tag = (NBTTagCompound) nbt;
      instance.setXp(tag.getInt("xp"));
    }
  }

  final class RegistrationHelper extends CapabilityRegistrationHelper<ILevelHandler> {

    @Override
    public Class<ILevelHandler> getJavaClass() {
      return ILevelHandler.class;
    }

    @Override
    public Capability.IStorage<ILevelHandler> getDefaultStorage() {
      return new DefaultStorage();
    }

    @Override
    public ILevelHandler getNewDefaultImpl() {
      return new DefaultImpl();
    }
  }
}
