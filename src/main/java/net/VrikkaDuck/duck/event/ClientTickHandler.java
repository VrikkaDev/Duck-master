package net.VrikkaDuck.duck.event;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.*;
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

    private void checkUnusedContainers(MinecraftClient mc){
        if(mc.player == null){
            return;
        }
        BlockPos ppos = mc.player.getBlockPos();

        // Remove containers that are further away from player than 10
        Configs.Actions.WORLD_CONTAINERS.entrySet().removeIf(entry -> !ppos.isWithinDistance(entry.getKey(), 10));
    }

    private int containerCheck = 0;
    private CompletableFuture<Void> future;
    private final AtomicBoolean found = new AtomicBoolean(false);

    private void checkNewContainers(MinecraftClient mc){

        if(future != null && !future.isDone()){
            return;
        }

        if(found.get()){
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.CONTAINERS));

            // To make sure buffer is still readable after reading identifier
            buf.writeByte(0);

            ClientNetworkHandler.sendAction(buf);

            found.set(false);
        }

        future = null;

        //todo need different system...

        containerCheck ++;
        if(containerCheck >= 20){
            containerCheck = 0;

            found.set(false);


            Box box = new Box(mc.player.getBlockPos()).expand(5);
            Stream<BlockEntity> blockEntities = BlockPos.stream(box).map(mc.world::getBlockEntity);

            // todo maybe make run in parallel at some point?
            future = CompletableFuture.allOf(
                    blockEntities
                            .map(blockEntity -> CompletableFuture.runAsync(() -> {
                                if(blockEntity == null){
                                    return;
                                }

                                if(ContainerType.fromBlockEntity(blockEntity).Value != -1){

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
