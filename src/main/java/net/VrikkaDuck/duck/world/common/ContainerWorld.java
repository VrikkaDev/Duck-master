package net.VrikkaDuck.duck.world.common;

import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.ServerPlayerManager;
import net.VrikkaDuck.duck.networking.packet.ContainerPacket;
import net.VrikkaDuck.duck.util.NbtUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

// TODO finish this class in 1.0.9 or 1.1.0

// CURRENTLY NOT IN USE
public class ContainerWorld {
    private final ServerPlayerEntity player;
    public Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
    private Map<UUID, Entity> entities = new HashMap<>();
    private int lastHashedBlockEntities = 0;
    private Instant lastReload = Instant.now();

    public ContainerWorld(ServerPlayerEntity player){
        this.player = player;
    }

    public void reload(){

        if(Duration.between(lastReload, Instant.now()).toMillis() > 500){
            lastReload = Instant.now();

            this.updateIfChanged();
        }
    }

    private BlockPos getLastBlockPos(){
        return (BlockPos) ServerPlayerManager.INSTANCE().getProperty(player.getUuid(), "playerLastBlockpos").orElse(new BlockPos(0,0,0));
    }

    private int getHashedBlockEntities(List<BlockEntity> blents){
        int l = 0;
        for (BlockEntity a : blents){

            if(a == null){
                continue;
            }

            l = (l+a.createNbtWithId().hashCode());
            l = Integer.hashCode(l);
        }
        return l;
    }

    private List<BlockEntity> getBlockEntitiesAround(BlockPos pos){
        Variables.PROFILER.start("containerWorld_getBlockEntities");
        Box box = new Box(pos).expand(5);

        //todo make parallel
        Stream<BlockEntity> blockEntities = BlockPos.stream(box).map(player.getWorld()::getBlockEntity).filter(Objects::nonNull);

        Variables.PROFILER.stop("containerWorld_getBlockEntities");
        return blockEntities.toList();
    }

    private void updateIfChanged(){

        Variables.PROFILER.start("containerWorld_updateIfChanged");

        List<BlockEntity> bents = getBlockEntitiesAround(getLastBlockPos());

        int hn = getHashedBlockEntities(bents);

        if(Objects.equals(hn, lastHashedBlockEntities)){
            return;
        }

        lastHashedBlockEntities = hn;

        Map<BlockPos, BlockEntity> diffBlocks = getDifferences(bents, blockEntities.values().stream().toList());


        Optional<ContainerPacket.ContainerS2CPacket> packet = NbtUtils.getContainerPacket(diffBlocks.keySet().stream().toList(), player);

        packet.ifPresent((p -> NetworkHandler.SendToClient(player, p)));


        blockEntities.clear();
        bents.forEach((be -> {
            if(be != null){
                blockEntities.put(be.getPos(), be);
            }
        }));



        Variables.PROFILER.stop("containerWorld_updateIfChanged");
        return;
    }

    private Map<BlockPos, BlockEntity> getDifferences(List<BlockEntity> be1, List<BlockEntity> be2){
        Variables.PROFILER.start("containerWorld_getDifferences");

        Variables.PROFILER.stop("containerWorld_getDifferences");
        return null;
    }
}
