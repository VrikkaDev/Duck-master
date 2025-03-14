package net.VrikkaDuck.duck.render.gui.inventory.blockentity;

import com.google.common.collect.ImmutableCollection;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.include.com.google.common.collect.Maps;

import java.util.HashMap;
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
    private final CrafterInventoryRenderer crafterInventoryRenderer = new CrafterInventoryRenderer();
    private final BrewingInventoryRenderer brewingInventoryRenderer = new BrewingInventoryRenderer();

    //:)


    private final Map<ContainerType, Integer> spacings = Map.ofEntries(
            Map.entry(ContainerType.NONE, 0),
            Map.entry(ContainerType.HOPPER, 55),
            Map.entry(ContainerType.DOUBLE_CHEST, 40),
            Map.entry(ContainerType.CHEST, 70),
            Map.entry(ContainerType.ENDER_CHEST, 50),
            Map.entry(ContainerType.DISPENSER, 41),
            Map.entry(ContainerType.FURNACE, 50),
            Map.entry(ContainerType.BEEHIVE, 30),
            Map.entry(ContainerType.CHISELED_BOOKSHELF, 176),
            Map.entry(ContainerType.CRAFTER, 50),
            Map.entry(ContainerType.BREWING_STAND, 100)
    );


    public ContainerGuiRenderer(){

    }

    public void render(DrawContext context){
        BlockPos pos = Configs.Actions.LOOKING_AT;

        Map.Entry<NbtCompound, ContainerType> entry;

       // Pair<NbtCompound, ContainerType> p = Configs.Actions.LOOKING_AT_BE_CLIENT.getOrDefault("Minecraft", new Pair<>(null, ContainerType.NONE));

        int amount = 0;
        int centerX = ScaledWidth() / 2;
        for (Map.Entry<String, Pair<NbtCompound, ContainerType>> e : Configs.Actions.LOOKING_AT_BE_CLIENT.entrySet()) {
            Pair<NbtCompound, ContainerType> p = e.getValue();
            if(p.getRight() != ContainerType.NONE && p.getLeft() != null && p.getRight() != null && p.getRight() != ContainerType.NONE){
                amount++;
            }
        }
        //System.out.println("Amount: " + amount);

        int i = amount == 1 ? 0 : -1;

        for (Map.Entry<String, Pair<NbtCompound, ContainerType>> e : Configs.Actions.LOOKING_AT_BE_CLIENT.entrySet()) {
            Pair<NbtCompound, ContainerType> p = e.getValue();
            if(Configs.Actions.WORLD_CONTAINERS.containsKey(pos)){
                entry = Configs.Actions.WORLD_CONTAINERS.get(pos);
            }else{
                if(p.getRight() != ContainerType.NONE && p.getLeft() != null && p.getRight() != null){
                    entry = Map.entry(p.getLeft(), p.getRight());
                }else{
                    return;
                }
            }

            if(entry == null){
                return;
            }

            /*int spacing = spacings.getOrDefault(e.getValue().getRight(), 1);
            int mx = centerX - (amount - 1) * spacing / 2;*/
            int spacing = spacings.getOrDefault(entry.getValue(), 1);
            //int mx = centerX - (amount - 1) * spacing / 2 + i * spacing;

            //TODO need to change if more than 2 containers are rendered
            int mx = centerX + i * spacing;

            ItemStack cis = new ItemStack(Items.WHITE_SHULKER_BOX);
            cis.setNbt(entry.getKey());

            switch (entry.getValue()){
                case HOPPER -> hopperInventoryRenderer.render(cis, mx - 60, ScaledHeight() / 2 + 32, e.getKey(), context);
                case DOUBLE_CHEST -> doubleChestInventoryRenderer.render(cis, mx - 96, ScaledHeight() / 2 + 60, e.getKey(), context);
                case CHEST, ENDER_CHEST -> shulkerInventoryRenderer.render(cis, mx - 96, ScaledHeight() / 2 + 30, true, e.getKey(), context);
                case DISPENSER -> dispenserInventoryRenderer.render(cis, mx - 34, ScaledHeight() / 2 - 43, e.getKey(), context);
                case FURNACE -> furnaceInventoryRenderer.render(entry.getKey(), mx - 59, ScaledHeight() / 2 + 30, e.getKey(), context);
                case BEEHIVE -> beehiveInventoryRenderer.render(entry.getKey(), mx, ScaledHeight() / 2, e.getKey(), context);
                case CHISELED_BOOKSHELF -> chiseledBookshelfInventoryRenderer.render(entry.getKey(), ScaledWidth() / 2, ScaledHeight() / 2, e.getKey(), context);
                case CRAFTER -> crafterInventoryRenderer.render(entry.getKey(), mx, ScaledHeight() / 2, e.getKey(), context);
                case BREWING_STAND -> brewingInventoryRenderer.render(entry.getKey(), mx, ScaledHeight() / 2, e.getKey(), context);
                default -> {}
            }

            //context.drawText(mc.textRenderer, e.getKey(), mx-80, ScaledHeight() / 2 - 60, 0xFFFFFF, true);

           // mx += spacing;
            i = 1;
        }

    }

    private int ScaledWidth(){
        return GuiUtils.getScaledWindowWidth();
    }
    private int ScaledHeight(){
        return GuiUtils.getScaledWindowHeight();
    }
}
