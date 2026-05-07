package com.retrospeck.rtsweaponry.mana;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.retrospeck.rtsweaponry.Config;
import com.retrospeck.rtsweaponry.ModComponents;
import com.retrospeck.rtsweaponry.RTSWeaponry;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ManaCommand {
    public static void register() {
        if (!Config.getBoolean("commands.mana.enabled"))
            return;

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("mana")
                    .requires(src -> Permissions.check(src, RTSWeaponry.MOD_ID + ".commands.mana", 2))
                    .then(CommandManager.literal("defaults")
                            .requires(src -> Permissions.check(src, RTSWeaponry.MOD_ID + ".commands.mana.defaults", 3))
                            // reset defaults
                            .then(CommandManager.literal("reset")
                                    .then(CommandManager.literal("all")
                                            .executes(context -> {
                                                MinecraftServer server = context.getSource().getServer();
                                                if (!ManaSystem.resetDefaultMax(server) || !ManaSystem.resetDefaultRegen(server))
                                                    throw new RuntimeException("Invalid config values");
                                                context.getSource().sendFeedback(() -> Text.literal("Successfully reset all default mana values").formatted(Formatting.YELLOW), true);
                                                return 1;
                                            })
                                    )
                                    .then(CommandManager.literal("manaLimit")
                                            .executes(context -> {
                                                if (!ManaSystem.resetDefaultMax(context.getSource().getServer()))
                                                    throw new RuntimeException("Invalid config values");
                                                context.getSource().sendFeedback(() -> Text.literal("Successfully reset default mana limit").formatted(Formatting.YELLOW), true);
                                                return 1;
                                            })
                                    )
                                    .then(CommandManager.literal("regenRate")
                                            .executes(context -> {
                                                if (!ManaSystem.resetDefaultRegen(context.getSource().getServer()))
                                                    throw new RuntimeException("Invalid config values");
                                                context.getSource().sendFeedback(() -> Text.literal("Successfully reset default mana regen rate").formatted(Formatting.YELLOW), true);
                                                return 1;
                                            })
                                    )
                            )
                            // set defaults
                            .then(CommandManager.literal("set")
                                    .then(CommandManager.literal("manaLimit")
                                            .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                    .executes(context -> {
                                                        int value = IntegerArgumentType.getInteger(context, "value");
                                                        if (!ManaSystem.setDefaultMax(value, context.getSource().getServer())) {
                                                            context.getSource().sendFeedback(() -> Text.literal("Enter a positive integer.").formatted(Formatting.RED), false);
                                                            return -1;
                                                        }
                                                        context.getSource().sendFeedback(() -> Text.literal("Set default mana limit to").formatted(Formatting.YELLOW)
                                                                .append(Text.literal(" " + value).formatted(Formatting.AQUA)), true);
                                                        return 1;
                                                    })
                                            )
                                    )
                                    .then(CommandManager.literal("regenRate")
                                            .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                    .executes(context -> {
                                                        int value = IntegerArgumentType.getInteger(context, "value");
                                                        if (!ManaSystem.setDefaultRegen(value, context.getSource().getServer())) {
                                                            context.getSource().sendFeedback(() -> Text.literal("Enter a positive integer.").formatted(Formatting.RED), false);
                                                            return -1;
                                                        }
                                                        context.getSource().sendFeedback(() -> Text.literal("Set default mana regen rate to").formatted(Formatting.YELLOW)
                                                                .append(Text.literal(" " + value).formatted(Formatting.AQUA)), true);
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            // get defaults
                            .then(CommandManager.literal("get")
                                    .then(CommandManager.literal("manaLimit")
                                            .executes(context -> {
                                                context.getSource().sendFeedback(() -> Text.literal("The current default mana limit is").formatted(Formatting.YELLOW)
                                                        .append(Text.literal(" " + ManaSystem.getDefaultMax()).formatted(Formatting.AQUA)), false);
                                                return 1;
                                            })
                                    )
                                    .then(CommandManager.literal("regenRate")
                                            .executes(context -> {
                                                context.getSource().sendFeedback(() -> Text.literal("The current default mana regen rate is").formatted(Formatting.YELLOW)
                                                        .append(Text.literal(" " + ManaSystem.getDefaultRegen()).formatted(Formatting.AQUA)), false);
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .then(CommandManager.literal("add")
                            .requires(src -> Permissions.check(src, RTSWeaponry.MOD_ID + ".commands.mana.add", 2))
                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                    .executes(context -> commandAddMana(context, EntityArgumentType.getPlayer(context, "player"), false))
                                    .then(CommandManager.argument("amount", IntegerArgumentType.integer())
                                            .executes(context -> commandAddMana(context, EntityArgumentType.getPlayer(context, "player"), true))
                                    )
                            )
                    )
                    .then(CommandManager.literal("set")
                            .requires(src -> Permissions.check(src, RTSWeaponry.MOD_ID + ".commands.mana.set", 2))
                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                    .then(CommandManager.literal("manaAmount")
                                            .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                    .executes(context -> {
                                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                        if (!ModComponents.MANA.get(player).setMana(IntegerArgumentType.getInteger(context, "value"))) {
                                                            context.getSource().sendFeedback(() -> Text.literal("Warning! ").formatted(Formatting.RED)
                                                                    .append(Text.literal("Setting a player's mana to a value below zero or greater than their limit may cause unintended side effects.").formatted(Formatting.YELLOW)), false);
                                                        }
                                                        context.getSource().sendFeedback(() -> Text.literal("Set ").formatted(Formatting.YELLOW)
                                                                .append(Text.literal(player.getName().getString()).formatted(Formatting.DARK_GREEN))
                                                                .append(Text.literal("'s mana amount to").formatted(Formatting.YELLOW))
                                                                .append(Text.literal(" " + ModComponents.MANA.get(player).getPlayerMana()).formatted(Formatting.AQUA)), true);
                                                        return 1;
                                                    })
                                            )
                                    )
                                    .then(CommandManager.literal("manaLimit")
                                            .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                    .executes(context -> {
                                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                        if (!ModComponents.MANA.get(player).setPlayerMax(IntegerArgumentType.getInteger(context, "value"))) {
                                                            context.getSource().sendFeedback(() -> Text.literal("Enter a positive integer.").formatted(Formatting.RED), false);
                                                        }
                                                        context.getSource().sendFeedback(() -> Text.literal("Set ").formatted(Formatting.YELLOW)
                                                                .append(Text.literal(player.getName().getString()).formatted(Formatting.DARK_GREEN))
                                                                .append(Text.literal("'s mana limit to").formatted(Formatting.YELLOW))
                                                                .append(Text.literal(" " + ModComponents.MANA.get(player).getPlayerMax()).formatted(Formatting.AQUA)), true);
                                                        return 1;
                                                    })
                                            )
                                    )
                                    .then(CommandManager.literal("regenRate")
                                            .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                                    .executes(context -> {
                                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                                        if (!ModComponents.MANA.get(player).setPlayerRegen(IntegerArgumentType.getInteger(context, "value"))) {
                                                            context.getSource().sendFeedback(() -> Text.literal("Enter a positive integer.").formatted(Formatting.RED), false);
                                                        }
                                                        context.getSource().sendFeedback(() -> Text.literal("Set ").formatted(Formatting.YELLOW)
                                                                .append(Text.literal(player.getName().getString()).formatted(Formatting.DARK_GREEN))
                                                                .append(Text.literal("'s mana regen rate to").formatted(Formatting.YELLOW))
                                                                .append(Text.literal(" " + ModComponents.MANA.get(player).getPlayerRegen()).formatted(Formatting.AQUA)), true);
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                    )
                    .then(CommandManager.literal("get")
                            .requires(src -> Permissions.check(src, RTSWeaponry.MOD_ID + ".commands.mana.get", 2))
                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                    .executes(context -> {
                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                        ManaComponentImpl playerManaInstance = ModComponents.MANA.get(player);
                                        context.getSource().sendFeedback(() -> Text.literal(player.getName().getString()).formatted(Formatting.DARK_GREEN)
                                                .append(Text.literal(" has").formatted(Formatting.YELLOW))
                                                .append(Text.literal(" " + playerManaInstance.getPlayerMana()).formatted(Formatting.AQUA))
                                                .append(Text.literal("/").formatted(Formatting.YELLOW))
                                                .append(Text.literal(playerManaInstance.getPlayerMax() + " mana ").formatted(Formatting.AQUA))
                                                .append(Text.literal("and has a regen rate of").formatted(Formatting.YELLOW))
                                                .append(Text.literal(" " + playerManaInstance.getPlayerRegen()).formatted(Formatting.AQUA)), false);
                                        return 1;
                                    })
                            )
                    )
            );
        });
    }

    public static int commandAddMana(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, boolean hasAmountArg) {
        int amount;
        ManaComponentImpl playerManaInstance = ModComponents.MANA.get(player);

        if (hasAmountArg)
            amount = IntegerArgumentType.getInteger(context, "amount");
        else
            amount = playerManaInstance.getPlayerRegen();

        int newMana = playerManaInstance.getPlayerMana() + amount;
        if (!playerManaInstance.addMana(amount)) {
            if (newMana < 0) {
                context.getSource().sendFeedback(() -> Text.literal("Player's mana is empty!").formatted(Formatting.RED), false);
            }
            else {
                context.getSource().sendFeedback(() -> Text.literal("Player's mana is full!").formatted(Formatting.RED), false);
            }
            return -1;
        }
        if (newMana > playerManaInstance.getPlayerMax()) {
            context.getSource().sendFeedback(() -> Text.literal("Given value brings player past their mana limit. Player's mana will be filled instead.").formatted(Formatting.RED), true);
        }
        else if (newMana < 0) {
            context.getSource().sendFeedback(() -> Text.literal("Given value brings player's mana amount below 0. Player's mana will be emptied instead.").formatted(Formatting.RED), true);
        }

        context.getSource().sendFeedback(() -> Text.literal("Added ").formatted(Formatting.YELLOW)
                .append(Text.literal(amount + " mana ").formatted(Formatting.AQUA))
                .append(Text.literal("to ").formatted(Formatting.YELLOW))
                .append(Text.literal(player.getName().getString()).formatted(Formatting.DARK_GREEN)), true);
        return 1;
    }
}
