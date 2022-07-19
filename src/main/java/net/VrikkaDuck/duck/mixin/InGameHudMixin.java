package net.VrikkaDuck.duck.mixin;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.VrikkaDuck.duck.config.Configs;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(at = @At("RETURN"), method = "render")
    private void render(CallbackInfo cb){
        if(Configs.Actions.RENDER_CONTAINER_TOOLTIP){
            if(Configs.Admin.INSPECT_CONTAINER.getBooleanValue()){
                if(Configs.Generic.INSPECT_CONTAINER.getKeybind().isKeybindHeld()){
                    ItemStack stack = Configs.Actions.CONTAINER_ITEM_STACK;
                    if(stack.getNbt() == null || !(stack.getNbt().getCompound("BlockEntityTag").contains("Items"))){
                        NbtCompound n = stack.getNbt();
                        NbtList lst = new NbtList();
                        NbtCompound a =  new NbtCompound();
                        a.put("Count", NbtByte.of((byte) 1));
                        lst.add(0,a);
                        NbtCompound b =  new NbtCompound();
                        b.put("Slot", NbtByte.of((byte) 1));
                        lst.add(1,b);
                        NbtCompound c =  new NbtCompound();
                        c.put("Count", NbtString.of("minecraft:air"));
                        lst.add(2,c);
                        n.getCompound("BlockEntityTag").put("Items", lst);
                        stack.setNbt(n);
                    }
                    RenderUtils.renderShulkerBoxPreview(stack, GuiUtils.getScaledWindowWidth() / 2 - 96,
                            GuiUtils.getScaledWindowHeight() / 2 + 30, true);
                }
            }
        }
    }
}
