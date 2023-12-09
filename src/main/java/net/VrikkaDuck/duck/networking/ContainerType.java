package net.VrikkaDuck.duck.networking;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;

public enum ContainerType {
    NONE(-1),
    CHEST(0),
    DOUBLE_CHEST(1),
    ENDER_CHEST(2),
    HOPPER(5),
    DISPENSER(7),
    FURNACE(10),
    BEEHIVE(15),
    CHISELED_BOOKSHELF(20),
    SHULKER(0);

    public final int value;
    ContainerType(int value){
        this.value = value;
    }

    public static ContainerType fromValue(int val){
        for(ContainerType t : ContainerType.values()){
            if (t.value == val){
                return t;
            }
        }
        return NONE;
    }

    public static ContainerType fromBlockEntity(BlockEntity blockEntity){

        if(blockEntity == null){
            return ContainerType.NONE;
        }

        BlockEntityType<?> type = blockEntity.getType();


        if(type.equals(BlockEntityType.CHEST) || type.equals(BlockEntityType.TRAPPED_CHEST) || type.equals(BlockEntityType.BARREL)) {

            if (blockEntity instanceof ChestBlockEntity sbEntity) {
                if (sbEntity.getCachedState().get(ChestBlock.CHEST_TYPE).equals(ChestType.SINGLE)) {
                    return ContainerType.CHEST;
                } else {
                    return ContainerType.DOUBLE_CHEST;
                }
            }

            return ContainerType.CHEST;
        }else if(type.equals(BlockEntityType.ENDER_CHEST)){
            return ContainerType.ENDER_CHEST;
        }else if(type.equals(BlockEntityType.HOPPER)){
            return ContainerType.HOPPER;
        }else if(type.equals(BlockEntityType.DROPPER) || type.equals(BlockEntityType.DISPENSER)){
            return ContainerType.DISPENSER;
        }else if(type.equals(BlockEntityType.SHULKER_BOX)){
            return ContainerType.SHULKER;
        }else if(blockEntity instanceof AbstractFurnaceBlockEntity){
            return ContainerType.FURNACE;
        }else if(type.equals(BlockEntityType.BEEHIVE)){
            return ContainerType.BEEHIVE;
        }else if(type.equals(BlockEntityType.CHISELED_BOOKSHELF)){
            return ContainerType.CHISELED_BOOKSHELF;
        }

        return ContainerType.NONE;
    }
}
