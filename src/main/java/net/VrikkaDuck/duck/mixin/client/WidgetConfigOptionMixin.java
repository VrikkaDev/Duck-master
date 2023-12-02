package net.VrikkaDuck.duck.mixin.client;

import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.gui.ConfigOptionChangeListenerButton;
import fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetKeybindSettings;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import net.VrikkaDuck.duck.config.client.IAdminConfigLevel;
import net.VrikkaDuck.duck.config.client.gui.ConfigOptionListenerResetConfigs;
import net.VrikkaDuck.duck.config.client.options.admin.DuckConfigLevel;
import net.VrikkaDuck.duck.config.client.options.generic.DuckConfigHotkeyToggleable;
import net.VrikkaDuck.duck.gui.ConfigGui;
import net.VrikkaDuck.duck.gui.DuckAdminConfigButtonBoolean;
import net.VrikkaDuck.duck.gui.DuckAdminConfigButtonLevel;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WidgetConfigOption.class, remap = false, priority = 500)
public abstract class WidgetConfigOptionMixin extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper>{

    @Shadow @Final protected IKeybindConfigGui host;

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
                DuckAdminConfigButtonBoolean optionButton = new DuckAdminConfigButtonBoolean(x, y, configWidth-63, configHeight, (IAdminConfigLevel) config);
                DuckAdminConfigButtonLevel levelButton = new DuckAdminConfigButtonLevel(x+configWidth/2,y,configWidth/2, configHeight, (IAdminConfigLevel) config);
                ButtonGeneric resetButton = this.createResetButton(x + configWidth + 2, y, (IConfigResettable) config);

                ConfigOptionChangeListenerButton listenerChange = new ConfigOptionChangeListenerButton((IConfigResettable) config, resetButton, null);
                ConfigOptionListenerResetConfig listenerReset = new ConfigOptionListenerResetConfig((IConfigResettable) config, new ConfigOptionListenerResetConfigs.ConfigResetterButton(optionButton,levelButton), resetButton, null);

                this.addButton(optionButton, listenerChange);
                this.addButton(levelButton, listenerChange);
                this.addButton(resetButton, listenerReset);
            } else if(config instanceof DuckConfigHotkeyToggleable toggleable){

                configWidth -= 22; // adjust the width to match other configs due to the settings widget
                IKeybind keybind = toggleable.getKeybind();
                ConfigButtonKeybind keybindButton = new ConfigButtonKeybind(x, y, configWidth, 20, keybind, this.host);
                x += configWidth + 2;

                this.addWidget(new WidgetKeybindSettings(x, y, 20, 20, keybind, config.getName(), this.parent, this.host.getDialogHandler()));
                x += 22;

                this.addButton(keybindButton, this.host.getButtonPressListener());

                //+configWidth/2
                ButtonGeneric activeButton = new ConfigButtonBoolean(x,y,configWidth/2, configHeight, toggleable);
                ButtonGeneric resetButton = this.createResetButton(x + configWidth /2 , y, (IConfigResettable) config);
                resetButton.setHoverStrings("Is activated true/false");

                ConfigOptionChangeListenerButton listenerChange = new ConfigOptionChangeListenerButton((IConfigResettable) config, resetButton, null);

                WidgetConfigOption.HotkeyedBooleanResetListener listenerReset = new WidgetConfigOption.HotkeyedBooleanResetListener(

                        toggleable, activeButton, keybindButton, resetButton, this.host);

                this.host.addKeybindChangeListener(listenerReset::updateButtons);


                this.addButton(activeButton, listenerChange);
                this.addButton(resetButton, listenerReset);
            }
        }
    }
}
