package com.retrospeck.rtsweaponry;

import com.retrospeck.rtsweaponry.abilities.dash.DashHandler;
import com.retrospeck.rtsweaponry.item.ModItemGroups;
import com.retrospeck.rtsweaponry.item.ModItems;
import com.retrospeck.rtsweaponry.item.ModParticles;
import com.retrospeck.rtsweaponry.item.custom.DaggerItem;
import com.retrospeck.rtsweaponry.item.custom.TeleportItem;
import com.retrospeck.rtsweaponry.mana.ManaCommand;
import com.retrospeck.rtsweaponry.mana.ManaSystem;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTSWeaponry implements ModInitializer {
	public static final String MOD_ID = "rtsweaponry";
	public static final String MOD_VERSION = "0.0.1";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Config.load(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + "/config.toml"));

		ManaSystem.initialize();
		DashHandler.initialize();

		registerCommands();

		ModParticles.registerModParticles();
		ModItemGroups.initialize();
		ModItems.registerModItems();
		DaggerItem.initialize();
		TeleportItem.initialize();
	}

	private void registerCommands() {
		if (!Config.getBoolean("commands.enabled")) return;

		// main mod command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal(RTSWeaponry.MOD_ID)
					.requires(src -> Permissions.check(src, RTSWeaponry.MOD_ID + ".commands.about", 0))
					.executes(context -> {
						context.getSource().sendFeedback(() -> Text.literal("This server is running " + RTSWeaponry.MOD_ID + " version " + RTSWeaponry.MOD_VERSION + "."), false);
						return 1;
					})
					.then(CommandManager.literal("reload")
							.requires(src -> Permissions.check(src, RTSWeaponry.MOD_ID + ".commands.reload", 3))
							.executes(Config::reload))
					);
		});
		ManaCommand.register();
	}
}