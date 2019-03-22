package me.sargunvohra.leveluphp.command;

import static net.minecraft.command.Commands.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.sargunvohra.leveluphp.core.PlayerLevelHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.util.text.ITextComponent;

@UtilityClass
class GetCommand {
  public LiteralArgumentBuilder<CommandSource> register(
      String literal, int permissionLevel, Function<PlayerLevelHandler, ITextComponent> executor) {
    val command = literal(literal).requires(source -> source.hasPermissionLevel(permissionLevel));

    command.executes(
        context -> {
          val source = context.getSource();
          source
              .asPlayer()
              .getCapability(PlayerLevelHandler.CAPABILITY)
              .ifPresent(handler -> source.sendFeedback(executor.apply(handler), true));
          return 0;
        });

    command.then(
        Commands.argument("player", EntityArgument.player())
            .executes(
                context -> {
                  val source = context.getSource();
                  EntityArgument.getPlayer(context, "player")
                      .getCapability(PlayerLevelHandler.CAPABILITY)
                      .ifPresent(handler -> source.sendFeedback(executor.apply(handler), true));
                  return 0;
                }));

    return command;
  }
}
