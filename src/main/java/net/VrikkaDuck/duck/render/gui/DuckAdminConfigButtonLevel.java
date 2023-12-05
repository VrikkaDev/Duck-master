package net.VrikkaDuck.duck.render.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.render.RenderUtils;
import net.VrikkaDuck.duck.config.client.IAdminConfigLevel;
import net.VrikkaDuck.duck.util.PermissionLevel;
import net.minecraft.client.gui.DrawContext;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DuckAdminConfigButtonLevel extends ButtonGeneric {

    private static List<String> hoverText(){
        return List.of("Minimum permission level,", "required to use this feature","" , "NORMAL,", "OP");
    }

    private final IAdminConfigLevel config;

    public DuckAdminConfigButtonLevel(int x, int y, int width, int height, IAdminConfigLevel config)
    {
        super(x+(width/2), y, width/2, height, "");
        this.config = config;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        config.togglePermissionLevel();

        this.updateDisplayString();

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateDisplayString()
    {
        String valueStr = PermissionLevel.fromInt(this.config.getPermissionLevel());
        this.displayString = GuiBase.TXT_WHITE + valueStr + GuiBase.TXT_RST;
    }
    @Override
    public void render(int mouseX, int mouseY, boolean selected, DrawContext context)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            int buttonStyle = this.getTextureOffset(this.hovered);

            RenderUtils.color(1f, 1f, 1f, 1f);
            RenderUtils.setupBlend();
            RenderUtils.setupBlendSimple();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();

            if (this.renderDefaultBackground)
            {
                this.bindTexture(BUTTON_TEXTURES);
                RenderUtils.drawTexturedRect(this.x, this.y, 0, 46 + buttonStyle * 20, this.width / 2, this.height);
                RenderUtils.drawTexturedRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + buttonStyle * 20, this.width / 2, this.height);
            }

            if (this.icon != null)
            {
                int offset = this.renderDefaultBackground ? 4 : 0;
                int x = this.alignment == LeftRight.LEFT ? this.x + offset : this.x + this.width - this.icon.getWidth() - offset;
                int y = this.y + (this.height - this.icon.getHeight()) / 2;
                int u = this.icon.getU() + buttonStyle * this.icon.getWidth();

                this.bindTexture(this.icon.getTexture());
                RenderUtils.drawTexturedRect(x, y, u, this.icon.getV(), this.icon.getWidth(), this.icon.getHeight());
            }

            if (StringUtils.isBlank(this.displayString) == false)
            {
                int y = this.y + (this.height - 8) / 2;
                int color = 0xE0E0E0;

                if (this.enabled == false)
                {
                    color = 0xA0A0A0;
                }
                else if (this.hovered)
                {
                    color = 0xFFFFA0;
                }

                if (this.textCentered)
                {
                    this.drawCenteredStringWithShadow(this.x + this.width / 2, y, color, this.displayString, context);
                }
                else
                {
                    int x = this.x + 6;

                    if (this.icon != null && this.alignment == LeftRight.LEFT)
                    {
                        x += this.icon.getWidth() + 2;
                    }

                    this.drawStringWithShadow(x, y, color, this.displayString, context);
                }
            }

            if(this.hovered){
                RenderUtils.drawHoverText(mouseX,mouseY,hoverText(), context);
            }

        }
    }
}
