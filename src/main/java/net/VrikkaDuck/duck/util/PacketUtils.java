package net.VrikkaDuck.duck.util;

import net.VrikkaDuck.duck.config.common.ServerConfigs;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.ServerPlayerManager;
import net.VrikkaDuck.duck.networking.packet.ContainerPacket;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PacketUtils {

    // todo THESE im gonna remove these once i get ContainerWorld working

    public static void createAndSendS2CContainer(World world, BlockEntity be){

        if(!ServerConfigs.Generic.INSPECT_CONTAINER.getBooleanValue()){
            return;
        }

        BlockPos pos = be.getPos();

        if(!be.hasWorld()){
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

                if(!hasPerm){
                    continue;
                }

                ContainerPacket.ContainerS2CPacket p = NbtUtils.getContainerPacket(List.of(pos), splayer).orElse(null);

                if(p == null){
                    return;
                }
                NetworkHandler.SendToClient(splayer, p);
            }
        }
    }
}
