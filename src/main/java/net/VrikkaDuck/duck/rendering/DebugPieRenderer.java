package net.VrikkaDuck.duck.rendering;

import fi.dy.masa.malilib.render.RenderUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.debug.DebugProfiler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.ColorHelper;

import java.util.List;

public class DebugPieRenderer {
    MinecraftClient mc;
    Window window;
    public DebugPieRenderer(){
        mc = MinecraftClient.getInstance();
        window = mc.getWindow();
    }

    public void render(DrawContext context){

        List<DebugProfiler.ProfileEntry> profileEntries = Variables.PROFILER.getProfilingResultAverage();

        if (profileEntries != null) {
            Variables.PROFILER.start("debugPie");
            DrawContext drawContext = new DrawContext(mc, mc.getBufferBuilders().getEntityVertexConsumers());
            this.drawProfilerResults(drawContext, profileEntries);
            drawContext.draw();
            Variables.PROFILER.stop("debugPie");
        }
    }

    private void drawProfilerResults(DrawContext context, List<DebugProfiler.ProfileEntry> list) {

        final int ONE = 10;//(5 + window.getHeight()/2 - 10) / 25;

        RenderUtils.setupBlend();
        RenderUtils.color(1f, 1f, 1f, 1f);

        RenderUtils.drawRect(window.getWidth()/2 - 120, 5, 115, window.getHeight()/2 - 10, ColorHelper.Argb.getArgb(190, 10, 10, 10));
        int i = ONE + 1;
        for(DebugProfiler.ProfileEntry entry : list){
            List<String> t = List.of(entry.methodName, "took average " + entry.getAverageElapsed()/1000 + " ms");

            context.getMatrices().loadIdentity();
            context.getMatrices().scale(0.6f, 0.6f, 0.6f);

            RenderUtils.renderText(window.getWidth() - 338, i, ColorHelper.Argb.getArgb(255, 255, 255, 255), t, context);

            i += ONE * 2 + 1;
        }
    }
}
