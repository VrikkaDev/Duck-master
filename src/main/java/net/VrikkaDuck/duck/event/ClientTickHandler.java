package net.VrikkaDuck.duck.event;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.packet.ContainerPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class ClientTickHandler implements IClientTickHandler {
    private BlockPos PREVIOUS_BLOCK;
    private HitResult blockHit;
    private Entity prevTargeted;
    private final ClientBlockHitHandler blockHitHandler = ClientBlockHitHandler.INSTANCE();
    @Override
    public void onClientTick(MinecraftClient mc) {
        if (mc.world != null && mc.player != null)
        {
            testBlockHit(mc);
            testEntityHit(mc);
            checkUnusedContainers(mc);
            checkNewContainers(mc);
        }
    }

    private void testBlockHit(MinecraftClient mc){
        this.blockHit = mc.cameraEntity.raycast(5, 0.0F, false);
        if(this.blockHit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) this.blockHit).getBlockPos();

            if(blockPos.equals(PREVIOUS_BLOCK)){
                return;
            }
            PREVIOUS_BLOCK = blockPos;

            this.blockHitHandler.lookingNewBlock(blockPos);
        }else{
            PREVIOUS_BLOCK = null;
            this.blockHitHandler.lookingNewBlock(null);
        }
    }

    private void testEntityHit(MinecraftClient mc){
        if(mc.targetedEntity == null){
            prevTargeted = null;
            return;
        }
        if(mc.targetedEntity == prevTargeted){
            return;
        }
        prevTargeted = mc.targetedEntity;
        this.blockHitHandler.lookingNewEntity(mc.targetedEntity);
    }

    private Instant unusedContainerLast = Instant.now();
    private void checkUnusedContainers(MinecraftClient mc){
        if(mc.player == null){
            return;
        }

        if(!Configs.Generic.INSPECT_CONTAINER.getBooleanValue()){
            return;
        }

        if(Duration.between(containerCheck, Instant.now()).toMillis() > 1000){
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

        // Don't send packets if inspect container isnt enabled
        if(!Configs.Generic.INSPECT_CONTAINER.getBooleanValue()){
            return;
        }


        if(future != null && !future.isDone()){
            return;
        }

        if(found.get()){

            ContainerPacket.ContainerC2SPacket packet = new ContainerPacket.ContainerC2SPacket(mc.player.getUuid(), mc.getCameraEntity().getBlockPos());
            NetworkHandler.SendToServer(packet);

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
