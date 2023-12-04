package net.VrikkaDuck.duck.networking.packet;

import net.VrikkaDuck.duck.networking.ErrorLevel;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ErrorPacket {
    public record ErrorS2CPacket(UUID uuid, ErrorLevel level, Text error) implements FabricPacket {

        public static final PacketType<ErrorS2CPacket> TYPE = PacketType.create(new Identifier("duck", "s2c/error"), ErrorS2CPacket::read);

        public static ErrorS2CPacket read(PacketByteBuf buf) {
            return new ErrorS2CPacket(buf.readUuid(), ErrorLevel.fromValue(buf.readVarInt()), buf.readText());
        }

        @Override
        public void write(PacketByteBuf buf) {
            buf.writeUuid(uuid);
            buf.writeVarInt(level.value);
            buf.writeText(error);
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }

    public record ErrorC2SPacket(UUID uuid, ErrorLevel level, Text error) implements FabricPacket {

        public static final PacketType<ErrorC2SPacket> TYPE = PacketType.create(new Identifier("duck", "c2s/error"), ErrorC2SPacket::read);

        public static ErrorC2SPacket read(PacketByteBuf buf) {
            return new ErrorC2SPacket(buf.readUuid(), ErrorLevel.fromValue(buf.readVarInt()), buf.readText());
        }

        @Override
        public void write(PacketByteBuf buf) {
            buf.writeUuid(uuid);
            buf.writeVarInt(level.value);
            buf.writeText(error);
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }
}
