package net.VrikkaDuck.duck.render.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import net.VrikkaDuck.duck.config.client.IAdminConfigLevel;

public class DuckAdminConfigButtonBoolean extends ButtonGeneric {

    private final IAdminConfigLevel config;

    public DuckAdminConfigButtonBoolean(int x, int y, int width, int height, IAdminConfigLevel config)
    {
        super(x, y, width, height, "");
        this.config = config;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.config.toggleBooleanValue();
        this.updateDisplayString();

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateDisplayString()
    {
        String valueStr = String.valueOf(this.config.getBooleanValue());

        if (this.config.getBooleanValue())
        {
            this.displayString = GuiBase.TXT_DARK_GREEN + valueStr + GuiBase.TXT_RST;
        }
        else
        {
            this.displayString = GuiBase.TXT_DARK_RED + valueStr + GuiBase.TXT_RST;
        }
    }
}
