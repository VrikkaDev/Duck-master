package net.VrikkaDuck.duck.networking;

import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.config.common.ServerConfigs;
import net.VrikkaDuck.duck.debug.DebugPrinter;
import net.VrikkaDuck.duck.networking.packet.ContainerPacket;
import net.VrikkaDuck.duck.networking.packet.EntityPacket;
import net.VrikkaDuck.duck.util.NbtUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static net.VrikkaDuck.duck.util.NbtUtils.*;

public class NetworkHandler {
    public static class Server{
        public static void SendToClient(ServerPlayerEntity player, FabricPacket packet){

            System.out.println(packet.toString().length());
            if(packet.toString().length() > 900000){
                if(packet instanceof ContainerPacket.ContainerS2CPacket s2CPacket){
                    while (s2CPacket.toString().length() > 900000){
                        s2CPacket.nbtMap().remove(s2CPacket.nbtMap().keySet().stream().toList().get(0));
                    }
                }else{
                    Variables.LOGGER.warn("Tried to send packet too large");
                    return;
                }
            }

            ServerPlayNetworking.send(player, packet);
        }
        public static void SendBlockEntityToNearby(World world, BlockPos pos){
            if(!ServerConfigs.Generic.INSPECT_CONTAINER.getBooleanValue()){
                return;
            }

            Stream<? extends PlayerEntity> players = world.getPlayers().stream().filter(p -> {
                Optional<Object> _fp = ServerPlayerManager.INSTANCE().getProperty(p.getUuid(), "playerLastBlockpos");
                BlockPos _p = _fp.map(o -> (BlockPos) o).orElseGet(p::getBlockPos);

                return pos.isWithinDistance(_p, 10);
            });

            for(var player : players.toList()){
                if(player instanceof ServerPlayerEntity splayer){
                    boolean hasPerm = player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_CONTAINER.getPermissionLevel());
                    if(!hasPerm){continue;}
                    ContainerPacket.ContainerS2CPacket p = NbtUtils.getContainerPacket(List.of(pos), splayer).orElse(null);
                    if(p == null){
                        return;
                    }
                    NetworkHandler.Server.SendToClient(splayer, p);
                }
            }
        }

        public static void SendEntityToNearby(Entity entity){

            Stream<? extends PlayerEntity> players = entity.getWorld().getPlayers().stream().filter(p -> {
                Optional<Object> _fp = ServerPlayerManager.INSTANCE().getProperty(p.getUuid(), "playerLastBlockpos");
                BlockPos _p = _fp.map(o -> (BlockPos) o).orElseGet(p::getBlockPos);

                return entity.getPos().isInRange(_p.toCenterPos(), 10);
            });

            EntityDataType type = EntityDataType.fromEntity(entity);

            for(var player : players.toList()){
                if(player instanceof ServerPlayerEntity splayer){

                    if(splayer == entity){
                        continue;
                    }

                    NbtCompound compound;
                    switch (type){
                        case VILLAGER_TRADES -> compound = getVillagerTradesNbt((VillagerEntity) entity, splayer).orElse(new NbtCompound());
                        case PLAYER_INVENTORY -> compound = getPlayerInventoryNbt((ServerPlayerEntity) entity, splayer).orElse(new NbtCompound());
                        case MINECART_CHEST, MINECART_HOPPER -> compound = getMinecartContainerNbt((AbstractMinecartEntity) entity, splayer).orElse(new NbtCompound());
                        default -> {continue;}
                    }


                    compound.putUuid("uuid", entity.getUuid());
                    compound.putInt("entityType",  type.value);

                    NbtList _l = new NbtList();
                    _l.add(compound);
                    NbtCompound tc = new NbtCompound();
                    tc.put("entities", _l);

                    EntityPacket.EntityS2CPacket p = new EntityPacket.EntityS2CPacket(splayer.getUuid(), tc);

                    NetworkHandler.Server.SendToClient(splayer, p);
                }
            }
        }
    }
    public static class Client{
        public static void SendToServer(FabricPacket packet){
            DebugPrinter.DebugPrint(packet, Configs.Debug.PRINT_PACKETS_C2S.getBooleanValue());
            ClientPlayNetworking.send(packet);
        }
    }
}
