package net.VrikkaDuck.duck.util;

import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import net.VrikkaDuck.duck.Variables;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Arrays;

public class LitematicaUtils {
    public static void UpdateLitematicaWorld(){

        if(!net.VrikkaDuck.duck.config.client.Configs.Generic.LITEMATICA_SUPPORT.getBooleanValue()){
            net.VrikkaDuck.duck.config.client.Configs.Actions.THIRD_PARTY_WORLDS.remove("litematica");
            return;
        }

        Variables.PROFILER.start("litematicaUtils_updateLitematicaWorld");

        MinecraftClient mc = MinecraftClient.getInstance();
        if (Configs.Visuals.ENABLE_RENDERING.getBooleanValue() && mc.player != null) {

            WorldSchematic ws = SchematicWorldHandler.getSchematicWorld();

            net.VrikkaDuck.duck.config.client.Configs.Actions.THIRD_PARTY_RENDER_LAYERS.put("litematica", DataManager.getRenderLayerRange());
            net.VrikkaDuck.duck.config.client.Configs.Actions.THIRD_PARTY_WORLDS.put("litematica", ws);
        }else{
            net.VrikkaDuck.duck.config.client.Configs.Actions.THIRD_PARTY_WORLDS.remove("litematica");
        }
        Variables.PROFILER.stop("litematicaUtils_updateLitematicaWorld");
    }

}
