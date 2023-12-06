package net.VrikkaDuck.duck.event;

import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.packet.ContainerPacket;
import net.VrikkaDuck.duck.util.ChestUtils;
import net.VrikkaDuck.duck.util.NbtUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class ClientBlockHitHandler {
    public ClientBlockHitHandler(){
    }

    private static ClientBlockHitHandler instance;
    public static ClientBlockHitHandler INSTANCE(){
        if(instance == null){
            instance = new ClientBlockHitHandler();
        }
        return instance;
    }
    private static MinecraftClient mc = MinecraftClient.getInstance();

    private BlockPos PREVIOUS_BLOCK;

    public void reload(){

        HitResult blockHit = mc.cameraEntity.raycast(5, 0.0F, false);
        if(blockHit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) blockHit).getBlockPos();

            if(blockPos.equals(PREVIOUS_BLOCK)){
                return;
            }
            PREVIOUS_BLOCK = blockPos;

            lookingNewBlock(blockPos);
        }else{
            PREVIOUS_BLOCK = null;
            lookingNewBlock(null);
        }
    }

    public void tick(){
        checkNewContainers(mc);
        checkUnusedContainers(mc);

        Variables.PROFILER.start("clientBlockHitHandler_reloadRaycast");
        this.reload();
        Variables.PROFILER.stop("clientBlockHitHandler_reloadRaycast");
    }

    public void lookingNewBlock(BlockPos blockPos){

        Configs.Actions.LOOKING_AT = blockPos;

        if(blockPos == null){
            resetAll();
            return;
        }


        if(Configs.Actions.LOOKING_AT_ENTITY != null){
            return;
        }

        BlockEntity blockEntity = mc.world.getBlockEntity(blockPos);

        if(blockEntity == null){
            resetAll();
            return;
        }


        ContainerType ct = ContainerType.fromBlockEntity(blockEntity);

        // Checks if targeted block is one of the supported container blocks
        if(ct != ContainerType.NONE){
            
            if(ct == ContainerType.DOUBLE_CHEST){
                ChestBlockEntity chestbe = (ChestBlockEntity)blockEntity;
                BlockState bs = chestbe.getCachedState();
                if((bs.get(ChestBlock.CHEST_TYPE).equals(ChestType.RIGHT))){
                    Configs.Actions.LOOKING_AT = ChestUtils.getOtherChestBlockPos(mc.world, blockPos);
                }
            }

            if(!Configs.Generic.INSPECT_CONTAINER.getKeybind().isKeybindHeld()){
                return;
            }

            Configs.Actions.RENDER_DOUBLE_CHEST_TOOLTIP = ct.value;
        }else{
            resetAll();
        }

    }

    private void resetAll(){
        this.PREVIOUS_BLOCK = null;
    }

    private Instant unusedContainerLast = Instant.now();
    private void checkUnusedContainers(MinecraftClient mc){
        if(mc.player == null){
            return;
        }

        if(!Configs.Generic.INSPECT_CONTAINER.getBooleanValue()){
            return;
        }

        if(Duration.between(unusedContainerLast, Instant.now()).toMillis() > 500){
            unusedContainerLast = Instant.now();
            BlockPos ppos = mc.getCameraEntity().getBlockPos();

            // Remove containers that are further away from player than 10
            Configs.Actions.WORLD_CONTAINERS.entrySet().removeIf(entry -> !ppos.isWithinDistance(entry.getKey(), 10));
        }
    }

    private Instant containerCheck = Instant.now();
    private CompletableFuture<Void> future;
    private final AtomicBoolean found = new AtomicBoolean(false);

    private void checkNewContainers(MinecraftClient mc){

        // Don't send packets if inspect container isn't enabled
        if(!Configs.Generic.INSPECT_CONTAINER.getBooleanValue() || !Configs.Admin.INSPECT_CONTAINER.getBooleanValue()){
            return;
        }


        if(future != null && !future.isDone()){
            return;
        }

        if(found.get()){

            ContainerPacket.ContainerC2SPacket packet = new ContainerPacket.ContainerC2SPacket(mc.player.getUuid(), mc.getCameraEntity().getBlockPos());
            NetworkHandler.Client.SendToServer(packet);

            found.set(false);
        }

        future = null;

        //todo need different system...

        if(Duration.between(containerCheck, Instant.now()).toMillis() > 400){
            containerCheck = Instant.now();

            found.set(false);


            Box box = new Box(mc.getCameraEntity().getBlockPos()).expand(5);
            Stream<BlockEntity> blockEntities = BlockPos.stream(box).map(mc.world::getBlockEntity);

            future = CompletableFuture.allOf(
                    blockEntities
                            .map(blockEntity -> CompletableFuture.runAsync(() -> {
                                if(blockEntity == null){
                                    return;
                                }

                                if(ContainerType.fromBlockEntity(blockEntity).value != -1){

                                    if(Configs.Actions.WORLD_CONTAINERS.containsKey(blockEntity.getPos())){
                                        return;
                                    }

                                    found.set(true);
                                }

                            }))
                            .toArray(CompletableFuture[]::new)
            );
        }
    }
}
