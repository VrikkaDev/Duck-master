package net.VrikkaDuck.duck.event;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
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
            this.blockHit = mc.player.raycast(5, 0.0F, false);
            if(Configs.Generic.INSPECT_SHULKER.getKeybind().isKeybindHeld()){
                if(this.blockHit.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = ((BlockHitResult) this.blockHit).getBlockPos();
                    BlockState blockState = mc.world.getBlockState(blockPos);

                    if(blockPos.equals(PREVIOUS_BLOCK)){
                        return;
                    }

                    PREVIOUS_BLOCK = blockPos;

                    if(blockState.getBlock().getName().toString().contains("shulker")){
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.SHULKER));
                        buf.writeBlockPos(blockPos);
                        ClientNetworkHandler.sendAction(buf, PacketTypes.SHULKER);
                    }else{
                        Configs.Actions.RENDER_SHULKER_TOOLTIP = false;
                    }
                }else{
                    Configs.Actions.RENDER_SHULKER_TOOLTIP = false;
                }
            }else{
                Configs.Actions.RENDER_SHULKER_TOOLTIP = false;
                PREVIOUS_BLOCK = null;
            }
        }
    }
}
