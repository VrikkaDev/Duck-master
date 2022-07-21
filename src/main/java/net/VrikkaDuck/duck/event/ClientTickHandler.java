package net.VrikkaDuck.duck.event;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class ClientTickHandler implements IClientTickHandler {
    private BlockPos PREVIOUS_BLOCK;
    private HitResult blockHit;
    private Entity prevTargeted;
    private ClientBlockHitHandler blockHitHandler = ClientBlockHitHandler.INSTANCE();
    @Override
    public void onClientTick(MinecraftClient mc) {
        if (mc.world != null && mc.player != null)
        {
            testBlockHit(mc);
            testEntityHit(mc);
        }
    }

    private void testBlockHit(MinecraftClient mc){
        this.blockHit = mc.player.raycast(5, 0.0F, false);
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
}
