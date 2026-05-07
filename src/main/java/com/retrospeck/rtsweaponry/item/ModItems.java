package com.retrospeck.rtsweaponry.item;

import com.retrospeck.rtsweaponry.RTSWeaponry;
import com.retrospeck.rtsweaponry.item.custom.DaggerItem;
import com.retrospeck.rtsweaponry.item.custom.TeleportItem;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static final Item ASPECT_OF_THE_END = register(
            "aspect_of_the_end",
            settings -> new TeleportItem(ToolMaterial.DIAMOND, 7f, 3f, settings),
            new Item.Settings()
    );
  
    public static final Item DIAMOND_DAGGER = register(
            "diamond_dagger",
            settings -> new DaggerItem(ToolMaterial.DIAMOND, 1.25f, -2f, settings),
            new Item.Settings()
    );

    public static final Item NETHERITE_DAGGER = register(
            "netherite_dagger",
            settings -> new DaggerItem(ToolMaterial.NETHERITE, 2f, -2f, settings),
            new Item.Settings()
    );

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(RTSWeaponry.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static void registerModItems() {
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.RTS_WEAPONS_KEY)
                .register(entries -> {
                    /*
                    entries.add(WOODEN_DAGGER);
                    entries.add(STONE_DAGGER);
                    entries.add(IRON_DAGGER);
                    entries.add(GOLD_DAGGER);
                    */
                    entries.add(ASPECT_OF_THE_END);
                    entries.add(DIAMOND_DAGGER);
                    entries.add(NETHERITE_DAGGER);
                });
    }
}
