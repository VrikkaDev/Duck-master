package net.VrikkaDuck.duck.networking.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class AdminPacket {

    // The nbt is formatted like this {boolean request, NbtList options{String optionName, boolean optionValue, int optionPermissionLevel} }

    public record AdminS2CPacket(UUID uuid, NbtCompound nbtCompound) implements FabricPacket {

        public static final PacketType<AdminS2CPacket> TYPE = PacketType.create(new Identifier("duck", "s2c/admin"), AdminS2CPacket::read);

        public static AdminS2CPacket read(PacketByteBuf buf) {
            return new AdminS2CPacket(buf.readUuid(), buf.readNbt());
        }

        @Override
        public void write(PacketByteBuf buf) {
            buf.writeUuid(uuid);
            buf.writeNbt(nbtCompound);
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }

    public record AdminC2SPacket(UUID uuid, NbtCompound nbtCompound) implements FabricPacket {

        public static final PacketType<AdminC2SPacket> TYPE = PacketType.create(new Identifier("duck", "c2s/admin"), AdminC2SPacket::read);

        public static AdminC2SPacket read(PacketByteBuf buf) {
            return new AdminC2SPacket(buf.readUuid(), buf.readNbt());
        }

        @Override
        public void write(PacketByteBuf buf) {
            buf.writeUuid(uuid);
            buf.writeNbt(nbtCompound);
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }

}
