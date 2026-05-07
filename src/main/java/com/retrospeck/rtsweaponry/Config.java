package com.retrospeck.rtsweaponry;

import com.moandjiezana.toml.Toml;
import com.mojang.brigadier.context.CommandContext;
import com.retrospeck.rtsweaponry.abilities.dash.DashHandler;
import com.retrospeck.rtsweaponry.item.custom.DaggerItem;
import com.retrospeck.rtsweaponry.item.custom.TeleportItem;
import com.retrospeck.rtsweaponry.mana.ManaSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class Config {
    private static Toml toml;
    public static final String CONFIG_VERSION = "0.0.6";

    // generates the default config file if not present
    public static boolean load(Path path) {
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.copy(Objects.requireNonNull(Config.class.getResourceAsStream("/config.toml")), path);
            } catch (IOException e) {
                RTSWeaponry.LOGGER.error("Failed to create default config.", e);
            }
        }
        try {
            toml = new Toml().read(Files.newInputStream(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config file", e);
        }
        // replace old config files
        if (!toml.getString("version").equals(CONFIG_VERSION)) {
            RTSWeaponry.LOGGER.warn("Config file version does not match current mod version. Generating a new config...");
            try {
                Files.move(path, path.resolveSibling(path.getFileName() + ".old"), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(Objects.requireNonNull(Config.class.getResourceAsStream("/config.toml")), path);
            } catch (IOException e) {
                RTSWeaponry.LOGGER.error("Failed to generate a new config.", e);
            }
            try {
                toml = new Toml().read(Files.newInputStream(path));
            } catch (IOException e) {
                throw new RuntimeException("Failed to read config file", e);
            }
        }
        RTSWeaponry.LOGGER.info("Config file successfully initialized.");
        return true;
    }

    public static int reload(CommandContext<ServerCommandSource> context) {
        if (!load(FabricLoader.getInstance().getConfigDir().resolve(RTSWeaponry.MOD_ID + "/config.toml")))
            throw new RuntimeException("Failed to initialize config");
        boolean success = false;

        // all systems that need their variables to be reinitialized after config change
        success = ManaSystem.reload(context.getSource().getServer());
        success = DashHandler.loadValues();
        success = DaggerItem.initialize();
        success = TeleportItem.initialize();

        if (!success) {
            throw new RuntimeException("One or more values in config are invalid.");
        }

        context.getSource().sendFeedback(() -> Text.literal("Config reloaded successfully!").formatted(Formatting.GREEN), true);
        return 1;
    }

    public static long getLong(String key) { return toml.getLong(key); }
    public static double getDouble(String key) { return toml.getDouble(key); }
    public static boolean getBoolean(String key) { return toml.getBoolean(key); }
    public static String getString(String key) { return toml.getString(key); }
}
