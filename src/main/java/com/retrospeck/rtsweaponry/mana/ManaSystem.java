package com.retrospeck.rtsweaponry.mana;

import com.retrospeck.rtsweaponry.Config;
import com.retrospeck.rtsweaponry.ModComponents;
import com.retrospeck.rtsweaponry.RTSWeaponry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ManaSystem {
    private static int defaultMax;
    private static int defaultRegen; // how much mana is regenerated per cycle
    private static int ticksBetweenRegen;
    private static int tickCounter = 0;

    public static void initialize() {
        loadDefaultMax();
        loadDefaultRegen();
        resetRegenSpeed();

        if (ticksBetweenRegen > 0) {
            ServerTickEvents.START_SERVER_TICK.register(ManaSystem::onServerTick);
        }
        else {
            RTSWeaponry.LOGGER.warn("Mana regeneration will be disabled until config value ticksBetweenRegen is reloaded with a positive integer.");
        }
    }

    public static boolean reload(MinecraftServer server) {
        return resetDefaultMax(server) && resetDefaultRegen(server) && resetRegenSpeed();
    }

    private static void onServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter < ticksBetweenRegen) return;

        tickCounter = 0; // reset tick counter once number is reached
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ModComponents.MANA.get(player).regenerate();
        }
    }

    public static boolean resetDefaultMax(MinecraftServer server) {
        boolean success = loadDefaultMax();
        refreshAllMaxes(server);
        return success;
    }

    public static boolean resetDefaultRegen(MinecraftServer server) {
        boolean success = loadDefaultRegen();
        refreshAllRegen(server);
        return success;
    }

    private static boolean loadDefaultMax() {
        try {
            defaultMax = (int)(Config.getLong("mana.defaultManaCap"));
            if (defaultMax < 0)
                throw new ArithmeticException("Invalid config values");
        }
        catch (ArithmeticException e) {
            defaultMax = 0;
            RTSWeaponry.LOGGER.error("Configuration value defaultManaCap in [mana] is invalid! Expected a positive integer.");
            return false;
        }
        return true;
    }

    private static boolean loadDefaultRegen() {
        try {
            defaultRegen = (int)(Config.getLong("mana.defaultRegenRate"));
            if (defaultRegen < 0)
                throw new ArithmeticException("Invalid config values");
        }
        catch (ArithmeticException e) {
            defaultRegen = 0;
            RTSWeaponry.LOGGER.error("Configuration value defaultRegenRate in [mana] is invalid! Expected a positive integer.");
            return false;
        }
        return true;
    }

    public static boolean resetRegenSpeed() {
        try {
            ticksBetweenRegen = (int)(Config.getLong("mana.ticksBetweenRegen"));
            if (ticksBetweenRegen < 0)
                throw new ArithmeticException("Invalid config values");
        }
        catch (ArithmeticException e) {
            ticksBetweenRegen = -1;
            RTSWeaponry.LOGGER.error("Configuration value ticksBetweenRegen in [mana] is invalid! Expected a positive integer.");
            return false;
        }
        return true;
    }

    public static boolean setDefaultMax(int newDefaultMax, MinecraftServer server) {
        if (newDefaultMax < 0)
            return false;
        defaultMax = newDefaultMax;
        refreshAllMaxes(server);
        return true;
    }

    public static boolean setDefaultRegen(int newDefaultRegen, MinecraftServer server) {
        if (newDefaultRegen < 0)
            return false;
        defaultRegen = newDefaultRegen;
        refreshAllRegen(server);
        return true;
    }

    public static void refreshAllMaxes(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ModComponents.MANA.get(player).refreshPlayerMax();
        }
    }

    public static void refreshAllRegen(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ModComponents.MANA.get(player).refreshPlayerMax();
        }
    }

    public static int getDefaultMax() { return defaultMax; }
    public static int getDefaultRegen() { return defaultRegen; }
}
