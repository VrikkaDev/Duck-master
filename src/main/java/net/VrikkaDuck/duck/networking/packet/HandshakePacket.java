package net.VrikkaDuck.duck.networking.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class HandshakePacket {
    public record HandshakeS2CPacket(UUID uuid, String duckVersion) implements FabricPacket {

        public static final PacketType<HandshakeS2CPacket> TYPE = PacketType.create(new Identifier("duck", "s2c/handshake"), HandshakeS2CPacket::read);

        public static HandshakeS2CPacket read(PacketByteBuf buf) {
            return new HandshakeS2CPacket(buf.readUuid(), buf.readString());
        }

        @Override
        public void write(PacketByteBuf buf) {
            buf.writeUuid(uuid);
            buf.writeString(duckVersion);
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }
    public record HandshakeC2SPacket(UUID uuid, String duckVersion) implements FabricPacket {

        public static final PacketType<HandshakeC2SPacket> TYPE = PacketType.create(new Identifier("duck", "c2s/handshake"), HandshakeC2SPacket::read);

        public static HandshakeC2SPacket read(PacketByteBuf buf) {
            return new HandshakeC2SPacket(buf.readUuid(), buf.readString());
        }

        @Override
        public void write(PacketByteBuf buf) {
            buf.writeUuid(uuid);
            buf.writeString(duckVersion);
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }
}
