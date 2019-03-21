package me.sargunvohra.leveluphp.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.sargunvohra.leveluphp.capability.PlayerLevelHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;

import java.util.function.BiConsumer;

import static net.minecraft.command.Commands.literal;

@UtilityClass
class SetIntCommand {
  public LiteralArgumentBuilder<CommandSource> register(
      String literal, int permissionLevel, BiConsumer<PlayerLevelHandler, Integer> executor) {
    val command = literal(literal).requires(source -> source.hasPermissionLevel(permissionLevel));

    command.then(
        Commands.argument("amount", IntegerArgumentType.integer(0))
            .executes(
                context -> {
                  val player = context.getSource().asPlayer();
                  val amount = IntegerArgumentType.getInteger(context, "amount");

                  player
                      .getCapability(PlayerLevelHandler.CAPABILITY)
                      .ifPresent(handler -> executor.accept(handler, amount));

                  return 0;
                }));

    command.then(
        Commands.argument("players", EntityArgument.players())
            .then(
                Commands.argument("amount", IntegerArgumentType.integer(0))
                    .executes(
                        context -> {
                          val players = EntityArgument.getPlayers(context, "players");
                          val amount = IntegerArgumentType.getInteger(context, "amount");

                          for (val player : players) {
                            player
                                .getCapability(PlayerLevelHandler.CAPABILITY)
                                .ifPresent(handler -> executor.accept(handler, amount));
                          }

                          return 0;
                        })));

    return command;
  }
}
