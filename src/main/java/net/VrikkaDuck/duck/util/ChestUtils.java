package net.VrikkaDuck.duck.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ChestUtils {
    public static BlockPos getOtherChestBlockPos(World world, BlockPos blockPos) {

        BlockState blockState = world.getBlockState(blockPos);

        Direction facing = blockState.get(ChestBlock.FACING);
        Direction otherBlockDir;

        ChestType type = blockState.get(ChestBlock.CHEST_TYPE);

        if (type == ChestType.LEFT) {
            otherBlockDir = facing.rotateYClockwise();
        } else {
            otherBlockDir = facing.rotateYCounterclockwise();
        }

        return blockPos.offset(otherBlockDir);
    }
}
