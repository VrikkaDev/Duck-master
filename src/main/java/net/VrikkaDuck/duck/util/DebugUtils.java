package net.VrikkaDuck.duck.util;

import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Configs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.NetworkSide;
import net.minecraft.text.Text;

public class DebugUtils {
    public static void DebugPrint(Object obj, boolean a){
        if(!Variables.DEBUG || !a || !GameWorld.hasClient()){
            return;
        }

        String ty = Configs.Debug.PRINT_TYPE.getOptionListValue().getStringValue();
        try {
            switch (ty) {
                case "none" -> {
                }
                case "message" -> {
                    assert MinecraftClient.getInstance().player != null;
                    MinecraftClient.getInstance().player.sendMessage(Text.of(obj.toString()));
                }
                case "console" -> {
                    Variables.LOGGER.info(obj.toString());
                }
                default -> {
                    return;
                }
            }
        }catch (Exception ignored){
        }
    }
}
