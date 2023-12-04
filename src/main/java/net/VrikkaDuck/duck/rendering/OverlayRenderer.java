package net.VrikkaDuck.duck.rendering;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.EntityDataType;
import net.VrikkaDuck.duck.util.GuiRenderUtils;
import net.VrikkaDuck.duck.util.InvUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOfferList;

import java.util.List;
import java.util.Map;

public class OverlayRenderer {

    private DebugPieRenderer debugPieRenderer = new DebugPieRenderer();

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

        if(Variables.DEBUG && Configs.Debug.DRAW_DEBUG_PIE.getBooleanValue()){
            renderDebugPie(context);
        }

        if (!Configs.Generic.isAnyPressed()) {
            return;
        }

        if(Configs.Generic.INSPECT_CONTAINER.isKeybindHeld()){
            renderContainerOverlay(context);
        }
        // If any pressed INSPECT_CONTAINER excluded
        if(Configs.Generic.isAnyPressed(List.of(Configs.Generic.INSPECT_CONTAINER))){
            renderEntityOverlay(context);
        }
    }

    private void renderDebugPie(DrawContext context){
        this.debugPieRenderer.render(context);
    }

    private void renderContainerOverlay(DrawContext context){

        BlockPos pos = Configs.Actions.LOOKING_AT;

        if(!Configs.Actions.WORLD_CONTAINERS.containsKey(pos)){
            return;
        }

        Map.Entry<NbtCompound, ContainerType> entry = Configs.Actions.WORLD_CONTAINERS.get(pos);

        if(entry == null){
            return;
        }

        ItemStack cis = new ItemStack(Items.WHITE_SHULKER_BOX);
        cis.setNbt(entry.getKey());

        switch (entry.getValue()){
            case HOPPER -> GuiRenderUtils.renderHopperPreview(cis,
                    GuiUtils.getScaledWindowWidth() / 2 - (52 + 8), GuiUtils.getScaledWindowHeight() / 2 + (16 + 16), true, context);
            case DOUBLE_CHEST -> GuiRenderUtils.renderDoubleChestPreview(cis,
                    GuiUtils.getScaledWindowWidth() / 2 - 96, GuiUtils.getScaledWindowHeight() / 2 + 60, true, context);
            case CHEST, ENDER_CHEST -> RenderUtils.renderShulkerBoxPreview(cis, GuiUtils.getScaledWindowWidth() / 2 - 96,
                    GuiUtils.getScaledWindowHeight() / 2 + 30, true, context);
            case DISPENSER -> GuiRenderUtils.renderDispenserPreview(cis, GuiUtils.getScaledWindowWidth() / 2 - 34, GuiUtils.getScaledWindowHeight() / 2 - 43, context);
            case FURNACE -> GuiRenderUtils.renderFurnacePreview(entry.getKey(), GuiUtils.getScaledWindowWidth() / 2 - 59,
                    GuiUtils.getScaledWindowHeight() / 2 + 30, context);
            case BEEHIVE -> GuiRenderUtils.renderBeehivePreview(entry.getKey(), GuiUtils.getScaledWindowWidth() / 2,
                    GuiUtils.getScaledWindowHeight() / 2, context);
            default -> {}
        }
    }

    private void renderEntityOverlay(DrawContext context){
        MinecraftClient mc = MinecraftClient.getInstance();

        if(Configs.Actions.LOOKING_AT_ENTITY == null){
            return;
        }

        Map.Entry<NbtCompound, EntityDataType> entry = Configs.Actions.WORLD_ENTITIES.get(Configs.Actions.LOOKING_AT_ENTITY);

        if(entry == null){
            return;
        }

        switch (entry.getValue()){
            case PLAYER_INVENTORY -> {
                if(Configs.Generic.INSPECT_PLAYER_INVENTORY.isKeybindHeld()){
                    GuiRenderUtils.renderPlayerInventory(InvUtils.fromNbt(entry.getKey()), GuiUtils.getScaledWindowWidth() / 2,
                            GuiUtils.getScaledWindowHeight() / 2, context);
                }
            }
            case VILLAGER_TRADES -> {
                if(Configs.Generic.INSPECT_VILLAGER_TRADES.isKeybindHeld()){
                    GuiRenderUtils.renderTrades(new TradeOfferList(entry.getKey()), GuiUtils.getScaledWindowWidth() / 2,
                            GuiUtils.getScaledWindowHeight() / 2, context);
                }
            }
            case MINECART_CHEST, MINECART_HOPPER -> {

                if(Configs.Generic.INSPECT_MINECART_CONTAINERS.isKeybindHeld()){

                    ItemStack cis = new ItemStack(Items.WHITE_SHULKER_BOX);
                    cis.setNbt(entry.getKey());

                    if(entry.getValue() == EntityDataType.MINECART_CHEST){
                        RenderUtils.renderShulkerBoxPreview(cis, GuiUtils.getScaledWindowWidth() / 2 - 96,
                                GuiUtils.getScaledWindowHeight() / 2 + 30, true, context);
                    }else{
                        GuiRenderUtils.renderHopperPreview(cis,
                                GuiUtils.getScaledWindowWidth() / 2 - (52 + 8), GuiUtils.getScaledWindowHeight() / 2 + (16 + 16), true, context);
                    }
                }
            }
            default -> {}
        }
    }
}
