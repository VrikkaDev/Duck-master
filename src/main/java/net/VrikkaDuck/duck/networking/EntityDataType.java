package net.VrikkaDuck.duck.networking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public enum EntityDataType {
    NONE(0, EntityType.PAINTING),
    PLAYER_INVENTORY(1, EntityType.PLAYER),
    MINECART_CHEST(3, EntityType.CHEST_MINECART),
    MINECART_HOPPER(4, EntityType.HOPPER_MINECART),
    VILLAGER_TRADES(20, EntityType.VILLAGER);

    public final int value;
    public final EntityType<?> type;
    EntityDataType(int l,EntityType<?> t){
        value = l;
        type = t;
    }

    public static EntityDataType fromValue(int v){
        for(EntityDataType e : EntityDataType.values()){
            if(e.value == v){
                return e;
            }
        }
        return NONE;
    }

    public static EntityDataType fromEntity(Entity entity){

        for(EntityDataType edt : EntityDataType.values()){
            if(edt.type == entity.getType()){
                return edt;
            }
        }

        return NONE;
    }
}
