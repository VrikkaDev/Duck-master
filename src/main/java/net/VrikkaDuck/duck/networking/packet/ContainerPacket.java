package net.VrikkaDuck.duck.networking.packet;

import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;

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
