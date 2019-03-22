package me.sargunvohra.leveluphp.command;

import static net.minecraft.command.Commands.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.sargunvohra.leveluphp.LevelUpHp;
import me.sargunvohra.leveluphp.capability.PlayerLevelHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

@UtilityClass
public class RootCommand {

  private final int MIN_PERMISSION = 2;

  public LiteralArgumentBuilder<CommandSource> register() {
    val base = literal(LevelUpHp.MOD_ID);
    base.then(SetIntCommand.register("setxp", MIN_PERMISSION, PlayerLevelHandler::setXp));
    base.then(SetIntCommand.register("addxp", MIN_PERMISSION, PlayerLevelHandler::addXp));
    base.then(SetIntCommand.register("setlevel", MIN_PERMISSION, PlayerLevelHandler::setLevel));
    base.then(SetIntCommand.register("addlevel", MIN_PERMISSION, PlayerLevelHandler::addLevel));
    base.then(GetCommand.register("check", MIN_PERMISSION, RootCommand::checkCommand));
    return base;
  }

  private ITextComponent checkCommand(PlayerLevelHandler handler) {
    return new TextComponentString(
        String.format(
            "level: %d, xp: %d/%d", handler.getLevel(), handler.getXp(), handler.xpTarget()));
  }
}
