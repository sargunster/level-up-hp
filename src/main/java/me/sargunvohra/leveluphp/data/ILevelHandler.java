package me.sargunvohra.leveluphp.data;

import me.sargunvohra.svlib.capability.CapabilityRegistrationHelper;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Attach this capability to any entity that should gain hp levels by killing mobs (namely, Players)
 */
public interface ILevelHandler {

  int getXp();

  int setXp(int xp);

  default int addXp(int xp) {
    return setXp(getXp() + xp);
  }

  final class RegistrationHelper extends CapabilityRegistrationHelper<ILevelHandler> {

    @Override
    public Class<ILevelHandler> getJavaClass() {
      return ILevelHandler.class;
    }

    @Override
    public Capability.IStorage<ILevelHandler> getDefaultStorage() {
      return new Capability.IStorage<ILevelHandler>() {
        private static final String KEY_XP = "xp";

        @Override
        public INBTBase writeNBT(
            Capability<ILevelHandler> capability, ILevelHandler instance, EnumFacing side) {
          NBTTagCompound tag = new NBTTagCompound();
          tag.putInt(KEY_XP, instance.getXp());
          return tag;
        }

        @Override
        public void readNBT(
            Capability<ILevelHandler> capability,
            ILevelHandler instance,
            EnumFacing side,
            INBTBase nbt) {
          NBTTagCompound tag = (NBTTagCompound) nbt;
          instance.setXp(tag.getInt(KEY_XP));
        }
      };
    }

    @Override
    public ILevelHandler getNewDefaultImpl() {
      return new ILevelHandler() {

        private int xp;

        @Override
        public int getXp() {
          return xp;
        }

        @Override
        public int setXp(int xp) {
          return this.xp = xp;
        }
      };
    }
  }
}
