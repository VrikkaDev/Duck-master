package net.VrikkaDuck.duck.gui;

import com.google.common.collect.Lists;
import com.sun.jna.platform.unix.solaris.LibKstat;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.mixin.WidgetListBaseMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigGui extends GuiConfigsBase {

    private static ConfigGuiTab tab = ConfigGuiTab.GENERIC;
    public static List<?> listWidgets;
    private static List<String> hoverText(){
      return List.of("This feature is disabled", "in this server!");
    };
    private static boolean isOn = false;

    public ConfigGui()
    {
        super(10, 50, Variables.MODID, null, "duck.gui.title.configs", String.format("%s", Variables.MODVERSION));
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;

        for (ConfigGuiTab tab : ConfigGuiTab.values())
        {
            x += this.createButton(x, y, -1, tab);
        }
        for (ConfigOptionWrapper wrapper : this.getConfigs()){
            Variables.LOGGER.info(wrapper.getConfig());

        }
        for(Object a : this.children()){
            Variables.LOGGER.info(a);
        }
    }

    @Override
    public void drawContents(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.getListWidget().drawContents(matrixStack, mouseX, mouseY, partialTicks);
        if(ConfigGui.tab == ConfigGuiTab.ADMIN){
            return;
        }
        if (this.getListWidget() != null)
        {
            if(listWidgets != null) {
                for (Object widget : listWidgets) {
                    if (!(widget instanceof WidgetConfigOption) || widget == null) {
                        break;
                    }
                    WidgetConfigOption w = (WidgetConfigOption) widget;

                    isOn = false;
                    for(IConfigBase ob : Configs.Admin.OPTIONS){
                        if(ob.getName().equals(w.getEntry().getConfig().getName())){
                            isOn = ((ConfigBoolean)ob).getBooleanValue();
                            break;
                        }
                    }
                   // Variables.LOGGER.info(isOn);
                    if(isOn){
                        return;
                    }

                    RenderUtils.drawRect(w.getX(), w.getY(), w.getWidth(), w.getHeight(), 0x8F4F4F4F);
                    RenderUtils.drawOutline(w.getX()-1, w.getY(), w.getWidth()+1, w.getHeight(), 1, 0x2F6F6F6F);
                    if(w.isMouseOver(mouseX, mouseY)){
                        RenderUtils.drawHoverText(mouseX, mouseY, hoverText(), matrixStack);
                    }
                }
            }


        }
    }

    @Override
    protected WidgetListConfigOptions createListWidget(int listX, int listY)
    {
        Variables.LOGGER.info("ääää");
        return new WidgetListConfigOptions(listX, listY,
                this.getBrowserWidth(), this.getBrowserHeight(), this.getConfigWidth(), this.getZOffset(), this.useKeybindSearch(), this);
    }


    private int createButton(int x, int y, int width, ConfigGuiTab tab)
    {
        if(tab.equals(ConfigGuiTab.ADMIN)){
            if(!MinecraftClient.getInstance().player.hasPermissionLevel(Variables.PERMISSIONLEVEL)){
                return 0;
            }
        }

        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(ConfigGui.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth() + 2;
    }

    @Override
    protected int getConfigWidth()
    {
        ConfigGuiTab tab = ConfigGui.tab;

        if (tab == ConfigGuiTab.GENERIC)
        {
            return 120;
        }
        /*else if (tab == ConfigGuiTab.FIXES)
        {
            return 60;
        }
        else if (tab == ConfigGuiTab.LISTS)
        {
            return 200;
        }*/

        return 260;
    }

    @Override
    protected boolean useKeybindSearch()
    {
        return false;/*GuiConfigs.tab == ConfigGuiTab.TWEAKS ||
                GuiConfigs.tab == ConfigGuiTab.GENERIC_HOTKEYS ||
                GuiConfigs.tab == ConfigGuiTab.DISABLES;*/
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = ConfigGui.tab;

        if (tab == ConfigGuiTab.GENERIC)
        {
            configs = Configs.Generic.DEFAULT_OPTIONS;
        }
        else if (tab == ConfigGuiTab.ADMIN)
        {
            configs = Configs.Admin.DEFAULT_OPTIONS;
        }
        else
        {
            return Collections.emptyList();
        }

        return ConfigOptionWrapper.createFor(configs);
    }

   /* protected BooleanHotkeyGuiWrapper wrapConfig(FeatureToggle config)
    {
        return new BooleanHotkeyGuiWrapper(config.getName(), config, config.getKeybind());
    }*/
    private static List<ConfigOptionWrapper> getActiveConfigs(List<ConfigOptionWrapper> confs){
        for(ConfigOptionWrapper wr : confs){

        }
        return null;
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final ConfigGui parent;
        private final ConfigGuiTab tab;

        public ButtonListener(ConfigGuiTab tab, ConfigGui parent)
        {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            ConfigGui.tab = this.tab;
            this.parent.reCreateListWidget(); // apply the new config width
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    public enum ConfigGuiTab
    {
        GENERIC         ("duck.gui.button.config_gui.generic"),
        ADMIN         ("duck.gui.button.config_gui.admin");

        private final String translationKey;

        ConfigGuiTab(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }
    }
}
