package net.VrikkaDuck.duck.render.gui.inventory.blockentity;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public class ContainerGuiRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    private final HopperInventoryRenderer hopperInventoryRenderer = new HopperInventoryRenderer();
    private final DoubleChestInventoryRenderer doubleChestInventoryRenderer = new DoubleChestInventoryRenderer();
    private final ShulkerInventoryRenderer shulkerInventoryRenderer = new ShulkerInventoryRenderer();
    private final DispenserInventoryRenderer dispenserInventoryRenderer = new DispenserInventoryRenderer();
    private final FurnaceInventoryRenderer furnaceInventoryRenderer = new FurnaceInventoryRenderer();
    private final BeehiveInventoryRenderer beehiveInventoryRenderer = new BeehiveInventoryRenderer();
    private final ChiseledBookshelfInventoryRenderer chiseledBookshelfInventoryRenderer = new ChiseledBookshelfInventoryRenderer();

    public ContainerGuiRenderer(){
    }

    public void render(DrawContext context){
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
            case HOPPER -> hopperInventoryRenderer.render(cis, ScaledWidth() / 2 - 60, ScaledHeight() / 2 + 32, context);
            case DOUBLE_CHEST -> doubleChestInventoryRenderer.render(cis, ScaledWidth() / 2 - 96, ScaledHeight() / 2 + 60, context);
            case CHEST, ENDER_CHEST -> shulkerInventoryRenderer.render(cis, ScaledWidth() / 2 - 96, ScaledHeight() / 2 + 30, true, context);
            case DISPENSER -> dispenserInventoryRenderer.render(cis, ScaledWidth() / 2 - 34, ScaledHeight() / 2 - 43, context);
            case FURNACE -> furnaceInventoryRenderer.render(entry.getKey(), ScaledWidth() / 2 - 59, ScaledHeight() / 2 + 30, context);
            case BEEHIVE -> beehiveInventoryRenderer.render(entry.getKey(), ScaledWidth() / 2, ScaledHeight() / 2, context);
            case CHISELED_BOOKSHELF -> chiseledBookshelfInventoryRenderer.render(entry.getKey(), ScaledWidth() / 2, ScaledHeight() / 2, context);
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
