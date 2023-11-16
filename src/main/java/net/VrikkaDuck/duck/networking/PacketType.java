
package net.VrikkaDuck.duck.networking;

import net.minecraft.util.Identifier;

public class PacketType{

    public static Identifier typeToIdentifier(PacketTypes type){
        return new Identifier("vrikkaduck", type.toString().toLowerCase());
    }
    public static PacketTypes identifierToType(Identifier id){
        try {
            PacketTypes type = PacketTypes.valueOf(id.getPath().toUpperCase());
            return type;
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        return PacketTypes.CONTAINER;
    }
}
