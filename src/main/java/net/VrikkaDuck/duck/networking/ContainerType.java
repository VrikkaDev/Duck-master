package net.VrikkaDuck.duck.networking;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;

public enum ContainerType {
    CHEST(0),
    DOUBLE_CHEST(1),
    HOPPER(2),
    DISPENSER(3),
    SHULKER(0);

    public final int Value;
    private ContainerType(int value){
        this.Value = value;
    }

    public static ContainerType fromBlockEntity(BlockEntity blockEntity){

        BlockEntityType<?> type = blockEntity.getType();


        if(type.equals(BlockEntityType.CHEST) || type.equals(BlockEntityType.TRAPPED_CHEST) || type.equals(BlockEntityType.BARREL)){

            if(blockEntity instanceof ChestBlockEntity sbEntity) {
                if(sbEntity.getCachedState().get(ChestBlock.CHEST_TYPE).equals(ChestType.SINGLE)){
                    return ContainerType.CHEST;
                }else{
                    return ContainerType.DOUBLE_CHEST;
                }
            }

            return ContainerType.CHEST;
        }else if(type.equals(BlockEntityType.HOPPER)){
            return ContainerType.HOPPER;
        }else if(type.equals(BlockEntityType.DROPPER) || type.equals(BlockEntityType.DISPENSER)){
            return ContainerType.DISPENSER;
        }else if(type.equals(BlockEntityType.SHULKER_BOX)){
            return ContainerType.SHULKER;
        }

        return null;
    }
}
