package com.retrospeck.rtsweaponry.abilities.dash;

import com.retrospeck.rtsweaponry.RTSWeaponry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DashPayload() implements CustomPayload {
    public static final Identifier DASH_PAYLOAD_ID = Identifier.of(RTSWeaponry.MOD_ID, "dash");
    public static final CustomPayload.Id<DashPayload> ID = new CustomPayload.Id<>(DASH_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, DashPayload> CODEC =
            PacketCodec.of(
                    (buf, payload) -> { /* empty payload */ },
                    buf -> new DashPayload()
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}