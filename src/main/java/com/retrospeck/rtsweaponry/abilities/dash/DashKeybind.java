package com.retrospeck.rtsweaponry.abilities.dash;

import com.retrospeck.rtsweaponry.RTSWeaponry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class DashKeybind {
    public static final String CATEGORY = "key.categories." + RTSWeaponry.MOD_ID;
    public static KeyBinding DASH_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key." + RTSWeaponry.MOD_ID + ".dash",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            CATEGORY
    ));

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (DASH_KEY.wasPressed()) {
                ClientPlayNetworking.send(new DashPayload());
            }
        });
    }
}
