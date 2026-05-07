package com.retrospeck.rtsweaponry.item;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.retrospeck.rtsweaponry.RTSWeaponry.MOD_ID;

public class ModParticles {
    public static final SimpleParticleType BACKSTAB_PARTICLE = FabricParticleTypes.simple();

    public static void registerModParticles() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "backstab"), BACKSTAB_PARTICLE);
    }
}