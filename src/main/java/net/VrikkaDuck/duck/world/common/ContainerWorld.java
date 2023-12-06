package net.VrikkaDuck.duck.world.common;

import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.ServerPlayerManager;
import net.VrikkaDuck.duck.networking.packet.ContainerPacket;
import net.VrikkaDuck.duck.util.NbtUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// nevermind

// CURRENTLY NOT IN USE
public class ContainerWorld {
    private final ServerPlayerEntity player;
    public Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
    private Map<UUID, Entity> entities = new HashMap<>();
    private Pair<Integer, Map<BlockPos, Integer>> lastHashedBlockEntities;
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

    private Pair<Integer, Map<BlockPos, Integer>> getHashedBlockEntities(List<BlockEntity> blents){
        Map<BlockPos, Integer> _map = new HashMap<>();
        int l = 0;
        for (BlockEntity a : blents){

            if(a == null){
                continue;
            }
            int _i = a.createNbtWithId().hashCode();
            _map.put(a.getPos(), _i);
            l = (l+_i);
            l = Integer.hashCode(l);
        }
        return new Pair<>(l, _map);
    }

    private List<BlockEntity> getBlockEntitiesAround(BlockPos pos){
        Variables.PROFILER.start("containerWorld_getBlockEntities");
        Box box = new Box(pos).expand(5);

        //todo make parallel?
        Stream<BlockEntity> blockEntities = BlockPos.stream(box).map(player.getWorld()::getBlockEntity).filter(Objects::nonNull);

        Variables.PROFILER.stop("containerWorld_getBlockEntities");
        return blockEntities.toList();
    }

    private void updateIfChanged(){

        Variables.PROFILER.start("containerWorld_updateIfChanged");




        Variables.PROFILER.stop("containerWorld_updateIfChanged");
        return;
    }

    private void updateBlockEntities(){
        List<BlockEntity> bents = getBlockEntitiesAround(getLastBlockPos());

        Pair<Integer, Map<BlockPos, Integer>> hn = getHashedBlockEntities(bents);

        if(lastHashedBlockEntities == null){
            lastHashedBlockEntities = hn;

            Optional<ContainerPacket.ContainerS2CPacket> packet = NbtUtils.getContainerPacket(bents.stream().map(BlockEntity::getPos).toList(), player);

            packet.ifPresent((p -> NetworkHandler.Server.SendToClient(player, p)));
            return;
        }

        if(Objects.equals(hn.getLeft(), lastHashedBlockEntities.getLeft())){
            return;
        }

        List<BlockPos> diffBlocks = getDifferences(hn.getRight(), lastHashedBlockEntities.getRight());

        lastHashedBlockEntities = hn;

        Optional<ContainerPacket.ContainerS2CPacket> packet = NbtUtils.getContainerPacket(diffBlocks, player);

        packet.ifPresent((p -> NetworkHandler.Server.SendToClient(player, p)));


        blockEntities.clear();
        bents.forEach((be -> {
            if(be != null){
                blockEntities.put(be.getPos(), be);
            }
        }));
    }

    private List<BlockPos> getDifferences(Map<BlockPos, Integer> be1, Map<BlockPos, Integer> be2){
        Variables.PROFILER.start("containerWorld_getDifferences");

        List<BlockPos> differences = new ArrayList<>();
        for (Map.Entry<BlockPos, Integer> entry : be1.entrySet()) {
            Integer ehash = be2.get(entry.getKey());
            if (ehash == null || !be2.containsValue(entry.getValue())) {
                differences.add(entry.getKey());
            }
        }

        Variables.PROFILER.stop("containerWorld_getDifferences");
        return differences;
    }
}
