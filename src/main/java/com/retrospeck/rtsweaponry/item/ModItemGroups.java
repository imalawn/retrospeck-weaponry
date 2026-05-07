package com.retrospeck.rtsweaponry.item;


import com.retrospeck.rtsweaponry.RTSWeaponry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final RegistryKey<ItemGroup> RTS_WEAPONS_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(RTSWeaponry.MOD_ID, "item_group"));
    public static final ItemGroup RTS_WEAPONS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.ASPECT_OF_THE_END))
            .displayName(Text.translatable("itemGroup.rts_weapons"))
            .build();

    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, RTS_WEAPONS_KEY, RTS_WEAPONS);
    }
}
