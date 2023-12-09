package net.VrikkaDuck.duck.render;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.debug.DebugPrinter;
import net.VrikkaDuck.duck.networking.EntityDataType;
import net.VrikkaDuck.duck.render.debug.DebugRenderer;
import net.VrikkaDuck.duck.render.gui.inventory.blockentity.ContainerGuiRenderer;
import net.VrikkaDuck.duck.render.gui.inventory.entity.EntityInventoryRenderer;
import net.VrikkaDuck.duck.util.GuiRenderUtils;
import net.VrikkaDuck.duck.util.InvUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.village.TradeOfferList;

import java.util.List;
import java.util.Map;

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
