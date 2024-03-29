package net.VrikkaDuck.duck.networking.packet;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.UUID;

public class ContainerPacket {
    public record ContainerS2CPacket(UUID uuid, Map<BlockPos,NbtCompound> nbtMap) implements FabricPacket {

        public static final PacketType<ContainerS2CPacket> TYPE = PacketType.create(new Identifier("duck", "s2c/container"), ContainerS2CPacket::read);

        public static ContainerS2CPacket read(PacketByteBuf buf) {
            return new ContainerS2CPacket(buf.readUuid(), buf.readMap(PacketByteBuf::readBlockPos, PacketByteBuf::readNbt));
        }

        @Override
        public void write(PacketByteBuf buf) {
            buf.writeUuid(uuid);
            buf.writeMap(nbtMap, PacketByteBuf::writeBlockPos, PacketByteBuf::writeNbt);
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }


    public record ContainerC2SPacket(UUID uuid, BlockPos pos) implements FabricPacket {

        public static final PacketType<ContainerC2SPacket> TYPE = PacketType.create(new Identifier("duck", "c2s/container"), ContainerC2SPacket::read);

        public static ContainerC2SPacket read(PacketByteBuf buf) {
            return new ContainerC2SPacket(buf.readUuid(), buf.readBlockPos());
        }


        @Override
        public void write(PacketByteBuf buf) {
            buf.writeUuid(uuid);
            buf.writeBlockPos(pos);
        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }
}
