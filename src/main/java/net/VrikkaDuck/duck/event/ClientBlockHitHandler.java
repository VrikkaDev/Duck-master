package net.VrikkaDuck.duck.event;

import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class ClientBlockHitHandler {
    public ClientBlockHitHandler(){
    }

    public static ClientBlockHitHandler INSTANCE(){
        return new ClientBlockHitHandler();
    }

    public void lookingNewBlock(BlockPos blockPos, MinecraftClient mc){

        if(blockPos == null){
            resetAll();
            return;
        }

        BlockEntity blockEntity = mc.world.getBlockEntity(blockPos);

        if(blockEntity == null){
            return;
        }

        resetAll();

        if(blockEntity.getType().equals(BlockEntityType.SHULKER_BOX) ||
                blockEntity.getType().equals(BlockEntityType.BARREL) ||
                blockEntity.getType().equals(BlockEntityType.CHEST)){

            Configs.Actions.RENDER_CONTAINER_TOOLTIP = true;

            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.CONTAINER));

            buf.writeBlockPos(blockPos);

            ClientNetworkHandler.sendAction(buf);
        }else if(blockEntity.getType().equals(BlockEntityType.FURNACE) ||
                blockEntity.getType().equals(BlockEntityType.FURNACE) ||
                blockEntity.getType().equals(BlockEntityType.SMOKER)) {


            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.FURNACE));

            buf.writeBlockPos(blockPos);

            ClientNetworkHandler.sendAction(buf);
        }

    }
    private void resetAll(){
        Configs.Actions.RENDER_CONTAINER_TOOLTIP = false;
        Configs.Actions.RENDER_FURNACE_TOOLTIP = false;
    }
}
