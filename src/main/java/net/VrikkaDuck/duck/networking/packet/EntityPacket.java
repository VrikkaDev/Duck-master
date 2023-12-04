package net.VrikkaDuck.duck.networking.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class EntityPacket {
    public record EntityS2CPacket(UUID uuid, NbtCompound nbt) implements FabricPacket {

        public static final PacketType<EntityS2CPacket> TYPE = PacketType.create(new Identifier("duck", "s2c/entity"), EntityS2CPacket::read);

        public static EntityS2CPacket read(PacketByteBuf buf) {
            return new EntityS2CPacket(buf.readUuid(), buf.readNbt());
        }

        @Override
        public void write(PacketByteBuf buf) {
            buf.writeUuid(uuid);
            buf.writeNbt(nbt);
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }

    public record EntityC2SPacket(UUID uuid, BlockPos playerpos) implements FabricPacket {

        public static final PacketType<EntityC2SPacket> TYPE = PacketType.create(new Identifier("duck", "c2s/entity"), EntityC2SPacket::read);

        public static EntityC2SPacket read(PacketByteBuf buf) {
            return new EntityC2SPacket(buf.readUuid(), buf.readBlockPos());
        }

        @Override
        public void write(PacketByteBuf buf) {
            buf.writeUuid(uuid);
            buf.writeBlockPos(playerpos);
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }
}
