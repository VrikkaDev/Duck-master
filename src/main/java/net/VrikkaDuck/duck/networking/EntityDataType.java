package net.VrikkaDuck.duck.networking;

public enum EntityDataType {
    NONE(0),
    PLAYER_INVENTORY(1),
    VILLAGER_TRADES(5);

    public final int value;
    EntityDataType(int l){
        value = l;
    }

    public static EntityDataType fromValue(int v){
        for(EntityDataType e : EntityDataType.values()){
            if(e.value == v){
                return e;
            }
        }
        return NONE;
    }
}
