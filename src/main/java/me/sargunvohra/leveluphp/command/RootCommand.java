package me.sargunvohra.leveluphp.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.sargunvohra.leveluphp.LevelUpHp;
import me.sargunvohra.leveluphp.capability.PlayerLevelHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import static net.minecraft.command.Commands.literal;

@SuppressWarnings("ALL")
@UtilityClass
public class RootCommand {
  public LiteralArgumentBuilder<CommandSource> register() {
    val base = literal(LevelUpHp.MOD_ID);
    base.then(SetIntCommand.register("setxp", 2, PlayerLevelHandler::setXp));
    base.then(SetIntCommand.register("addxp", 2, PlayerLevelHandler::addXp));
    base.then(SetIntCommand.register("setlevel", 2, PlayerLevelHandler::setLevel));
    base.then(SetIntCommand.register("addlevel", 2, PlayerLevelHandler::addLevel));
    base.then(GetCommand.register("check", 2, RootCommand::checkCommand));
    return base;
  }

  private ITextComponent checkCommand(PlayerLevelHandler handler) {
    return new TextComponentString(
        String.format(
            "level: %d, xp: %d/%d", handler.getLevel(), handler.getXp(), handler.xpTarget()));
  }
}
