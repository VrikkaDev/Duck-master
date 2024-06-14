package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.debug.DebugPrinter;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {

    @Unique
    private static BrewingStandBlockEntity prev = null;

    @Inject(method = "tick", at=@At("HEAD"))
    private static void duck$tick(World world, BlockPos pos, BlockState state, BrewingStandBlockEntity blockEntity, CallbackInfo ci){

        if (prev != null){
            NetworkHandler.Server.SendBlockEntityToNearby(prev.getWorld(), prev.getPos());
            prev = null;
        }

        ItemStack itemStack = blockEntity.inventory.get(4);
        if (blockEntity.fuel <= 0 && itemStack.isOf(Items.BLAZE_POWDER)) {
            NetworkHandler.Server.SendBlockEntityToNearby(world, pos);
            return;
        }



        boolean bl = BrewingStandBlockEntity.canCraft(blockEntity.inventory);
        boolean bl2 = blockEntity.brewTime > 0;
        ItemStack itemStack2 = blockEntity.inventory.get(3);
        if (bl2) {
            if (!bl || !itemStack2.isOf(blockEntity.itemBrewing)) {
                prev = blockEntity;
                return;
            }
        }


        if(blockEntity.brewTime >= 398){
            prev = blockEntity;
        }
        

        boolean[] bls = blockEntity.getSlotsEmpty();
        if (!Arrays.equals(bls, blockEntity.slotsEmptyLastTick)) {
            if (!(state.getBlock() instanceof BrewingStandBlock)) {
                return;
            }
            prev = blockEntity;
        }
    }

    @Inject(method = "craft", at=@At("RETURN"))
    private static void duck$craft(World world, BlockPos pos, DefaultedList<ItemStack> slots, CallbackInfo ci){
        NetworkHandler.Server.SendBlockEntityToNearby(world, pos);
    }
}
