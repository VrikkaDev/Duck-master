package net.VrikkaDuck.duck.event;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import io.netty.buffer.Unpooled;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.config.PacketType;
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
            //Variables.LOGGER.info("a");
            this.blockHit = mc.player.raycast(5, 0.0F, false);
            if(Configs.Generic.INSPECT_SHULKER.getKeybind().isKeybindHeld()){
               // Variables.LOGGER.info("b");
                if(this.blockHit.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = ((BlockHitResult) this.blockHit).getBlockPos();
                    BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
                    if(blockPos.equals(PREVIOUS_BLOCK)){
                        return;
                    }
                    PREVIOUS_BLOCK = blockPos;
                    if(blockState.getBlock().getName().toString().contains("shulker")){
                        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                        buf.writeBlockPos(blockPos);
                        ClientNetworkHandler.sendAction(buf, PacketType.SHULKER);
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
