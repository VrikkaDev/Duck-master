package net.VrikkaDuck.duck.event;

import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.EntityDataType;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.packet.EntityPacket;
import net.VrikkaDuck.duck.util.ChestUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class ClientBlockHitHandler {
    public ClientBlockHitHandler(){
    }

    public static ClientBlockHitHandler INSTANCE(){
        return new ClientBlockHitHandler();
    }
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public void reload(){

        HitResult blockHit = mc.cameraEntity.raycast(5, 0.0F, false);
        if(blockHit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) blockHit).getBlockPos();
            lookingNewBlock(blockPos);
        }
    }

    public void lookingNewBlock(BlockPos blockPos){

        Configs.Actions.LOOKING_AT = blockPos;

        if(blockPos == null){
            resetAll();
            return;
        }


        if(mc.targetedEntity != null){
            lookingNewEntity(mc.targetedEntity);
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

            Configs.Actions.RENDER_CONTAINER_TOOLTIP = true;
            Configs.Actions.RENDER_DOUBLE_CHEST_TOOLTIP = ct.value;

        }else if(blockEntity.getType().equals(BlockEntityType.FURNACE) ||
                blockEntity.getType().equals(BlockEntityType.BLAST_FURNACE) ||
                blockEntity.getType().equals(BlockEntityType.SMOKER)) {

            Configs.Actions.RENDER_CONTAINER_TOOLTIP = true;

        }else if(blockEntity.getType().equals(BlockEntityType.BEEHIVE)){
            Configs.Actions.RENDER_CONTAINER_TOOLTIP = true;
        }else{
            resetAll();
        }

    }
    public void lookingNewEntity(Entity entity){

        if(entity == null){
            resetEntity();
            return;
        }

        if(entity.getType().equals(EntityType.PLAYER)){
            if(Configs.Generic.INSPECT_PLAYER_INVENTORY.getBooleanValue()){
                EntityPacket.EntityC2SPacket packet = new EntityPacket.EntityC2SPacket(mc.player.getUuid(), EntityDataType.PLAYER_INVENTORY, entity.getUuid());

                NetworkHandler.SendToServer(packet);
            }
        }else if(entity.getType().equals(EntityType.VILLAGER)){
            if(Configs.Generic.INSPECT_VILLAGER_TRADES.getBooleanValue()){
                EntityPacket.EntityC2SPacket packet = new EntityPacket.EntityC2SPacket(mc.player.getUuid(), EntityDataType.VILLAGER_TRADES, entity.getUuid());

                NetworkHandler.SendToServer(packet);
            }
        } else{
            resetAll();
            return;
        }
        Configs.Actions.RENDER_CONTAINER_TOOLTIP = false;
    }
    private void resetAll(){
        Configs.Actions.RENDER_CONTAINER_TOOLTIP = false;
        if(mc.targetedEntity == null){
            resetEntity();
        }
    }
    private void resetEntity(){
        Configs.Actions.RENDER_PLAYER_INVENTORY_PREVIEW = false;
        Configs.Actions.RENDER_VILLAGER_TRADES = false;
    }
}
