package net.VrikkaDuck.duck.render.gui.inventory.entity;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.debug.DebugPrinter;
import net.VrikkaDuck.duck.networking.EntityDataType;
import net.VrikkaDuck.duck.util.GuiRenderUtils;
import net.VrikkaDuck.duck.util.InvUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.village.TradeOfferList;

import java.util.Map;

public class EntityInventoryRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    private final PlayerInventoryRenderer playerInventoryRenderer = new PlayerInventoryRenderer();
    private final VillagerTradeRenderer villagerTradeRenderer = new VillagerTradeRenderer();
    private final MinecartInventoryRenderer minecartInventoryRenderer = new MinecartInventoryRenderer();
    public EntityInventoryRenderer(){
    }

    public void render(DrawContext context){

        if(Configs.Actions.LOOKING_AT_ENTITY == null){
            return;
        }

        Map.Entry<NbtCompound, EntityDataType> entry = Configs.Actions.WORLD_ENTITIES.get(Configs.Actions.LOOKING_AT_ENTITY);

        if(entry == null){
            DebugPrinter.DebugPrint("%s entry doesnt exist".formatted(Configs.Actions.LOOKING_AT_ENTITY), Configs.Debug.PRINT_MISC.getBooleanValue());
            return;
        }

        switch (entry.getValue()){
            case PLAYER_INVENTORY -> {
                if(Configs.Generic.INSPECT_PLAYER_INVENTORY.isKeybindHeld() && Configs.Admin.INSPECT_PLAYER_INVENTORY.getBooleanValue()){
                    playerInventoryRenderer.render(InvUtils.fromNbt(entry.getKey()), ScaledWidth()/2,
                            ScaledHeight() / 2, context);
                }
            }
            case VILLAGER_TRADES -> {
                if(Configs.Generic.INSPECT_VILLAGER_TRADES.isKeybindHeld() && Configs.Admin.INSPECT_VILLAGER_TRADES.getBooleanValue()){
                    villagerTradeRenderer.render(new TradeOfferList(entry.getKey()), ScaledWidth() / 2,
                            ScaledHeight() / 2, context);
                }
            }
            case MINECART_CHEST, MINECART_HOPPER -> {

                if(Configs.Generic.INSPECT_MINECART_CONTAINERS.isKeybindHeld() && Configs.Admin.INSPECT_MINECART_CONTAINERS.getBooleanValue()){
                    minecartInventoryRenderer.render(entry, ScaledWidth()/2, ScaledHeight()/2, context);
                }
            }
            default -> {}
        }
    }
    private int ScaledWidth(){
        return GuiUtils.getScaledWindowWidth();
    }
    private int ScaledHeight(){
        return GuiUtils.getScaledWindowHeight();
    }
}
