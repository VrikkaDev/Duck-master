package net.VrikkaDuck.duck.event;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.config.PacketType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
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
                    if(blockState == null){
                        return;
                    }
                    if(blockPos.equals(PREVIOUS_BLOCK)){
                        return;
                    }
                    PREVIOUS_BLOCK = blockPos;
                    if(blockState.getBlock().getName().toString().contains("shulker")){
                        PacketByteBuf buf = PacketByteBufs.create();
                       // Variables.LOGGER.info(buf.readString());
                        buf.writeBlockPos(blockPos);//TODO: <- makes string unusable
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
