package net.VrikkaDuck.duck.render;

import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.render.debug.DebugRenderer;
import net.VrikkaDuck.duck.render.gui.inventory.blockentity.ContainerGuiRenderer;
import net.VrikkaDuck.duck.render.gui.inventory.entity.EntityInventoryRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class OverlayRenderer {

    private final DebugRenderer debugRenderer = new DebugRenderer();
    private final ContainerGuiRenderer containerGuiRenderer = new ContainerGuiRenderer();
    private final EntityInventoryRenderer entityInventoryRenderer = new EntityInventoryRenderer();

    OverlayRenderer(){
    }
    private static OverlayRenderer instance = null;

    public static OverlayRenderer INSTANCE(){
        if(instance == null){
            instance = new OverlayRenderer();
        }
        return instance;
    }

    public void render(DrawContext context){

        if(Variables.DEBUG){
            this.debugRenderer.render(context);
        }

        if (!Configs.Generic.isAnyPressed()) {
            return;
        }

        if(Configs.Generic.INSPECT_CONTAINER.isKeybindHeld()){
            Variables.PROFILER.start("overlayRenderer_renderContainerOverlay");
            containerGuiRenderer.render(context);
            Variables.PROFILER.stop("overlayRenderer_renderContainerOverlay");
        }
        // If any pressed INSPECT_CONTAINER excluded
        if(Configs.Generic.isAnyPressed(List.of(Configs.Generic.INSPECT_CONTAINER))){
            Variables.PROFILER.start("overlayRenderer_renderEntityOverlay");
            entityInventoryRenderer.render(context);
            Variables.PROFILER.stop("overlayRenderer_renderEntityOverlay");
        }
    }
}
