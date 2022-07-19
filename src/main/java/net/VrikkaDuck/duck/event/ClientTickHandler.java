package net.VrikkaDuck.duck.event;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class ClientTickHandler implements IClientTickHandler {
    private BlockPos PREVIOUS_BLOCK;
    private HitResult blockHit;
    @Override
    public void onClientTick(MinecraftClient mc) {
        if (mc.world != null && mc.player != null)
        {
            if(Configs.Generic.INSPECT_CONTAINER.getKeybind().isKeybindHeld()){
                this.blockHit = mc.player.raycast(5, 0.0F, false);
                if(this.blockHit.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = ((BlockHitResult) this.blockHit).getBlockPos();

                    if(blockPos.equals(PREVIOUS_BLOCK)){
                        return;
                    }

                    PREVIOUS_BLOCK = blockPos;

                    BlockEntity blockEntity = mc.world.getBlockEntity(blockPos);

                    if(blockEntity == null){
                        Configs.Actions.RENDER_CONTAINER_TOOLTIP = false;
                        return;
                    }

                    if(blockEntity.getType().equals(BlockEntityType.SHULKER_BOX)){

                        PacketByteBuf buf = PacketByteBufs.create();

                        buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.CONTAINER));

                        buf.writeBlockPos(blockPos);

                        ClientNetworkHandler.sendAction(buf, PacketTypes.CONTAINER);
                    }else{
                        Configs.Actions.RENDER_CONTAINER_TOOLTIP = false;
                    }
                }else{
                    Configs.Actions.RENDER_CONTAINER_TOOLTIP = false;
                }
            }else{
                Configs.Actions.RENDER_CONTAINER_TOOLTIP = false;
                PREVIOUS_BLOCK = null;
            }
        }
    }
}
