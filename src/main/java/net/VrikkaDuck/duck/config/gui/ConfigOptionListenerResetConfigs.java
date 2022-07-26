package net.VrikkaDuck.duck.config.gui;

import fi.dy.masa.malilib.gui.button.ButtonBase;

public class ConfigOptionListenerResetConfigs
{
    public static class ConfigResetterButton extends fi.dy.masa.malilib.config.gui.ConfigOptionListenerResetConfig.ConfigResetterBase
    {
        private final ButtonBase button;
        private final ButtonBase button2;

        public ConfigResetterButton(ButtonBase button, ButtonBase button2)
        {
            this.button = button;
            this.button2 = button2;
        }

        @Override
        public void resetConfigOption()
        {
            this.button.updateDisplayString();
            this.button2.updateDisplayString();
        }
    }
}