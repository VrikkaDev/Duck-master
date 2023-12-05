package net.VrikkaDuck.duck.render.debug;

import net.VrikkaDuck.duck.config.client.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

public class DebugRenderer {

    // RENDERERS
    private DebugPieRenderer debugPieRenderer;
    private DebugInfoRenderer debugInfoRenderer;
    public DebugRenderer(){
        this.debugPieRenderer = new DebugPieRenderer();
        this.debugInfoRenderer = new DebugInfoRenderer();
    }

    public void render(DrawContext context){
        if(Configs.Debug.DRAW_DEBUG_PIE.getBooleanValue()){
            this.debugPieRenderer.render(context);
        }
        if(Configs.Debug.DRAW_DEBUG_INFO.getBooleanValue()){
            this.debugInfoRenderer.render(context);
        }
    }
}
