
package net.VrikkaDuck.duck.config;

import net.minecraft.util.Identifier;

public class PacketType{

    public static Identifier typeToIdentifier(PacketTypes type){
        return new Identifier(         "vrikkaduck", type.toString().toLowerCase());
    }
    public static PacketTypes identifierToType(Identifier id){
        return PacketTypes.valueOf(id.getPath().toUpperCase());
    }
}
