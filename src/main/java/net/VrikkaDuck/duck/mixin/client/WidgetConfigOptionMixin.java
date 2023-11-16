package net.VrikkaDuck.duck.mixin.client;

import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import net.VrikkaDuck.duck.config.IConfigLevel;
import net.VrikkaDuck.duck.config.gui.ConfigOptionListenerResetConfigs;
import net.VrikkaDuck.duck.config.options.DuckConfigLevel;
import net.VrikkaDuck.duck.gui.ConfigGui;
import net.VrikkaDuck.duck.gui.DuckConfigButtonBoolean;
import net.VrikkaDuck.duck.gui.DuckConfigButtonLevel;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WidgetConfigOption.class, remap = false, priority = 500)
public abstract class WidgetConfigOptionMixin extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper>{
    public WidgetConfigOptionMixin(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex) {
        super(x, y, width, height, parent, entry, listIndex);
    }

    @Inject(method = "addConfigOption", at =
        @At(
                value = "RETURN",
                remap = false
        ), remap = false)
    public void addConfigOption(int x, int y, float zLevel, int labelWidth, int configWidth, IConfigBase config, CallbackInfo ci){

        if(!(MinecraftClient.getInstance().currentScreen instanceof ConfigGui)){
            return;
        }

        ConfigType type = config.getType();
        int configHeight = 20;
        if(type == null){
            if(config instanceof DuckConfigLevel){
                DuckConfigButtonBoolean optionButton = new DuckConfigButtonBoolean(x, y, configWidth-63, configHeight, (IConfigLevel) config);
                DuckConfigButtonLevel levelButton = new DuckConfigButtonLevel(x+configWidth/2,y,configWidth/2, configHeight, (IConfigLevel) config);
                ButtonGeneric resetButton = this.createResetButton(x + configWidth + 2, y, (IConfigResettable) config);

                ConfigOptionChangeListenerButton listenerChange = new ConfigOptionChangeListenerButton((IConfigResettable) config, resetButton, null);
                ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig((IConfigResettable) config, new ConfigOptionListenerResetConfigs.ConfigResetterButton(optionButton,levelButton), resetButton, null);

                this.addButton(optionButton, listenerChange);
                this.addButton(levelButton, listenerChange);
                this.addButton(resetButton, listenerReset);
            }
        }
    }
}
