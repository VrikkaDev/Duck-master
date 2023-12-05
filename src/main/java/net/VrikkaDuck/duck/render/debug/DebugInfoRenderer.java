package net.VrikkaDuck.duck.render.debug;

import fi.dy.masa.malilib.render.RenderUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.mixin.client.IClientWorldMixin;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ColorHelper;

import java.util.List;

public class DebugInfoRenderer {
    MinecraftClient mc;
    Window window;
    public DebugInfoRenderer(){
        mc = MinecraftClient.getInstance();
        window = mc.getWindow();
    }

    public void render(DrawContext context){
        Variables.PROFILER.start("debugInfoRenderer_render");

        Entity e = ((IClientWorldMixin)mc.world).duck_getEntityManager().getLookup().get(Configs.Actions.LOOKING_AT_ENTITY);
        ContainerType b = ContainerType.fromBlockEntity(Configs.Actions.LOOKING_AT == null ? null : mc.world.getBlockEntity(Configs.Actions.LOOKING_AT));

        context.getMatrices().push();

        List<String> _list = List.of(
                "LOOKING_AT_ENTITY:",
                "%s".formatted(Configs.Actions.LOOKING_AT_ENTITY),
                e == null ? "NULL" : e.getName().getString(),
                "--------------------------------",
                "LOOKING_AT_BLOCK_ENTITY:",
                "%s".formatted(Configs.Actions.LOOKING_AT),
                b.name()
        );

        RenderUtils.renderText(window.getWidth()/2 - 240, 10, ColorHelper.Argb.getArgb(255, 255, 255, 255), _list, context);

        context.getMatrices().pop();

        Variables.PROFILER.stop("debugInfoRenderer_render");
    }
}
