package net.VrikkaDuck.duck.mixin.client;

import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.util.GuiRenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    // thiS is STUPidD ;)
    @Inject(at = @At("RETURN"), method = "render")
    private void render(DrawContext context, float tickDelta, CallbackInfo cb) {
        if (Configs.Actions.RENDER_CONTAINER_TOOLTIP || Configs.Actions.RENDER_FURNACE_TOOLTIP
                || Configs.Actions.RENDER_BEEHIVE_PREVIEW || Configs.Actions.RENDER_PLAYER_INVENTORY_PREVIEW
                || Configs.Actions.RENDER_VILLAGER_TRADES) {

            ItemStack cis = Configs.Actions.WORLD_CONTAINERS.getOrDefault(Configs.Actions.LOOKING_AT, ItemStack.EMPTY);
            if(cis != null && !cis.isEmpty()) {
                if (handleRenderCondition(Configs.Actions.RENDER_CONTAINER_TOOLTIP, Configs.Generic.INSPECT_CONTAINER, ContainerType.DOUBLE_CHEST, context)) {
                    GuiRenderUtils.renderDoubleChestPreview(cis,
                            GuiUtils.getScaledWindowWidth() / 2 - 96, GuiUtils.getScaledWindowHeight() / 2 + 60, true, context);
                } else if (handleRenderCondition(Configs.Actions.RENDER_CONTAINER_TOOLTIP, Configs.Generic.INSPECT_CONTAINER, ContainerType.HOPPER, context)) {
                    GuiRenderUtils.renderHopperPreview(cis,
                            GuiUtils.getScaledWindowWidth() / 2 - (52 + 8), GuiUtils.getScaledWindowHeight() / 2 + (16 + 16), true, context);
                } else if (handleRenderCondition(Configs.Actions.RENDER_CONTAINER_TOOLTIP, Configs.Generic.INSPECT_CONTAINER, ContainerType.DISPENSER, context)) {
                    GuiRenderUtils.renderDispenserPreview(cis, GuiUtils.getScaledWindowWidth() / 2 - 34, GuiUtils.getScaledWindowHeight() / 2 - 43, context);
                } else if (handleRenderCondition(Configs.Actions.RENDER_CONTAINER_TOOLTIP, Configs.Generic.INSPECT_CONTAINER, ContainerType.CHEST, context)) {
                    RenderUtils.renderShulkerBoxPreview(cis, GuiUtils.getScaledWindowWidth() / 2 - 96,
                            GuiUtils.getScaledWindowHeight() / 2 + 30, true, context);
                }
            }

            if (handleRenderCondition(Configs.Actions.RENDER_FURNACE_TOOLTIP, Configs.Generic.INSPECT_FURNACE, null, context)) {
                GuiRenderUtils.renderFurnacePreview(Configs.Actions.FURNACE_NBT, GuiUtils.getScaledWindowWidth() / 2 - 59,
                        GuiUtils.getScaledWindowHeight() / 2 + 30, context);
            }

            if (handleRenderCondition(Configs.Actions.RENDER_BEEHIVE_PREVIEW, Configs.Generic.INSPECT_BEEHIVE, null, context)) {
                GuiRenderUtils.renderBeehivePreview(Configs.Actions.BEEHIVE_NBT, GuiUtils.getScaledWindowWidth() / 2,
                        GuiUtils.getScaledWindowHeight() / 2, context);
            }

            if (handleRenderCondition(Configs.Actions.RENDER_PLAYER_INVENTORY_PREVIEW, Configs.Generic.INSPECT_PLAYER_INVENTORY, null, context)) {
                GuiRenderUtils.renderPlayerInventory(Configs.Actions.TARGET_PLAYER_INVENTORY, GuiUtils.getScaledWindowWidth() / 2,
                        GuiUtils.getScaledWindowHeight() / 2, context);
            }

            if (handleRenderCondition(Configs.Actions.RENDER_VILLAGER_TRADES, Configs.Generic.INSPECT_VILLAGER_TRADES, null, context)) {
                GuiRenderUtils.renderTrades(Configs.Actions.VILLAGER_TRADES, GuiUtils.getScaledWindowWidth() / 2,
                        GuiUtils.getScaledWindowHeight() / 2, context);
            }
        }
    }

    @Unique
    private boolean handleRenderCondition(boolean renderFlag, ConfigHotkey inspectFlag, ContainerType containerType, DrawContext context) {
        return renderFlag && inspectFlag.getKeybind().isKeybindHeld()
                && (containerType == null || Configs.Actions.RENDER_DOUBLE_CHEST_TOOLTIP == containerType.Value);
    }
}
