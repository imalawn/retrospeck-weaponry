package com.retrospeck.rtsweaponry;

import com.retrospeck.rtsweaponry.mana.ManaComponentImpl;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class ModComponents implements EntityComponentInitializer {
    public static final ComponentKey<ManaComponentImpl> MANA =
            ComponentRegistry.getOrCreate(Identifier.of(RTSWeaponry.MOD_ID, "mana"), ManaComponentImpl.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(MANA, player -> new ManaComponentImpl(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}