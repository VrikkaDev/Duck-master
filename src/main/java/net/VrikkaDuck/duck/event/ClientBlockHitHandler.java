package net.VrikkaDuck.duck.event;

import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
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
        if(ct != null){

            if(!Configs.Generic.INSPECT_CONTAINER.getKeybind().isKeybindHeld()){
                return;
            }

            Configs.Actions.LOOKING_AT = blockPos;

            Configs.Actions.RENDER_CONTAINER_TOOLTIP = true;
            Configs.Actions.RENDER_DOUBLE_CHEST_TOOLTIP = ct.Value;
            //Configs.Actions.CONTAINER_ITEM_STACK = ItemStack.EMPTY;

            /*PacketByteBuf buf = PacketByteBufs.create();

            buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.CONTAINER));

            buf.writeBlockPos(blockPos);

            ClientNetworkHandler.sendAction(buf);*/

        }else if(blockEntity.getType().equals(BlockEntityType.FURNACE) ||
                blockEntity.getType().equals(BlockEntityType.BLAST_FURNACE) ||
                blockEntity.getType().equals(BlockEntityType.SMOKER)) {

            if(!Configs.Generic.INSPECT_FURNACE.getKeybind().isKeybindHeld()){
                return;
            }

            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.FURNACE));

            buf.writeBlockPos(blockPos);

            ClientNetworkHandler.sendAction(buf);
        }else if(blockEntity.getType().equals(BlockEntityType.BEEHIVE)){
            if(!Configs.Generic.INSPECT_BEEHIVE.getKeybind().isKeybindHeld()){
                return;
            }

            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.BEEHIVE));

            buf.writeBlockPos(blockPos);

            ClientNetworkHandler.sendAction(buf);
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
            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.PLAYERINVENTORY));
            buf.writeUuid(entity.getUuid());

            ClientNetworkHandler.sendAction(buf);
        }else if(entity.getType().equals(EntityType.VILLAGER)){
            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.VILLAGERTRADES));
            buf.writeInt(entity.getId());

            ClientNetworkHandler.sendAction(buf);
        } else{
            resetAll();
        }
    }
    private void resetAll(){
        Configs.Actions.RENDER_CONTAINER_TOOLTIP = false;
        Configs.Actions.RENDER_FURNACE_TOOLTIP = false;
        Configs.Actions.RENDER_BEEHIVE_PREVIEW = false;
        if(mc.targetedEntity == null){
            resetEntity();
        }
    }
    private void resetEntity(){
        Configs.Actions.RENDER_PLAYER_INVENTORY_PREVIEW = false;
        Configs.Actions.RENDER_VILLAGER_TRADES = false;
    }
}
