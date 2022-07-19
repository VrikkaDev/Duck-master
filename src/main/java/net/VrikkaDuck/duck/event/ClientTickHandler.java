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
    private ClientBlockHitHandler blockHitHandler = ClientBlockHitHandler.INSTANCE();
    @Override
    public void onClientTick(MinecraftClient mc) {
        if (mc.world != null && mc.player != null)
        {
            this.blockHit = mc.player.raycast(5, 0.0F, false);
            if(this.blockHit.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult) this.blockHit).getBlockPos();

                if(blockPos.equals(PREVIOUS_BLOCK)){
                    return;
                }
                PREVIOUS_BLOCK = blockPos;

                this.blockHitHandler.lookingNewBlock(blockPos, mc);
            }else{
                PREVIOUS_BLOCK = null;
                this.blockHitHandler.lookingNewBlock(null, mc);
            }
        }
    }
}
