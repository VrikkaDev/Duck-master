package net.VrikkaDuck.duck.mixin;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.*;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig;
import fi.dy.masa.malilib.config.options.BooleanHotkeyGuiWrapper;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import net.VrikkaDuck.duck.config.IConfigLevel;
import net.VrikkaDuck.duck.config.gui.ConfigOptionListenerResetConfigs;
import net.VrikkaDuck.duck.config.options.ConfigLevel;
import net.VrikkaDuck.duck.gui.DuckConfigButtonBoolean;
import net.VrikkaDuck.duck.gui.DuckConfigButtonLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WidgetConfigOption.class)
public class WidgetConfigOptionMixin extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper>{
    public WidgetConfigOptionMixin(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex) {
        super(x, y, width, height, parent, entry, listIndex);
    }

    @Inject(method = "addConfigOption", at = @At("TAIL"), remap = false)
    public void addConfigOption(int x, int y, float zLevel, int labelWidth, int configWidth, IConfigBase config, CallbackInfo ci){
        ConfigType type = config.getType();
        int configHeight = 20;
        if(type == null){
            if(config instanceof ConfigLevel){
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

    @Override
    public boolean wasConfigModified() {
        return true;
    }

    @Override
    public void applyNewValueToConfig() {
    }
}
