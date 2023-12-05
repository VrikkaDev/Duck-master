package net.VrikkaDuck.duck.render.gui;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.config.client.options.admin.DuckConfigLevel;
import net.VrikkaDuck.duck.util.GameWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.Collections;
import java.util.List;

public class ConfigGui extends GuiConfigsBase {

    private static ConfigGuiTab tab = ConfigGuiTab.GENERIC;
    public static List<?> listWidgets;
    private static List<String> hoverText(){
      return List.of("This feature is disabled", "in this server!");
    }
    static boolean isOn = false;

    public ConfigGui()
    {
        super(10, 50, Variables.MODID, null, "duck.gui.title.configs", String.format("%s", Variables.MODVERSION));
        tab = ConfigGuiTab.GENERIC;
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
    }

    @Override
    public void drawContents(DrawContext context, int mouseX, int mouseY, float partialTicks)
    {
        try {
            this.getListWidget().drawContents(context, mouseX, mouseY, partialTicks);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(ConfigGui.tab == ConfigGuiTab.ADMIN || ConfigGui.tab == ConfigGuiTab.DEBUG){
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
                            if(ob instanceof DuckConfigLevel) {
                                if(MinecraftClient.getInstance().world == null){
                                    isOn = true;
                                }else{
                                    isOn = GameWorld.hasPermissionLevel(((DuckConfigLevel) ob).getPermissionLevel(), mc) && ((DuckConfigLevel) ob).getBooleanValue();
                                }
                            }
                            break;
                        }
                    }
                    if(!isOn){
                        RenderUtils.drawRect(w.getX(), w.getY(), w.getWidth(), w.getHeight(), 0x8F4F4F4F);
                        if(w.isMouseOver(mouseX, mouseY) && mouseX > w.getWidth()/3){
                            RenderUtils.drawHoverText(mouseX, mouseY, hoverText(), context);
                        }
                    }
                }
            }


        }
    }

    @Override
    protected WidgetListConfigOptions createListWidget(int listX, int listY)
    {
        return new WidgetListConfigOptions(listX, listY,
                this.getBrowserWidth(), this.getBrowserHeight(), this.getConfigWidth(), 10f, this.useKeybindSearch(), this);
    }


    private int createButton(int x, int y, int width, ConfigGuiTab tab)
    {
        if(tab.equals(ConfigGuiTab.ADMIN)){
            if(MinecraftClient.getInstance().player == null){
                return 0;
            }
            if(!MinecraftClient.getInstance().player.hasPermissionLevel(Variables.PERMISSIONLEVEL)){
                return 0;
            }
        } else if(tab.equals(ConfigGuiTab.DEBUG) && !Variables.DEBUG){
            return 0;
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

        return 260;
    }

    @Override
    protected boolean useKeybindSearch()
    {
        return false;
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
        else if (tab == ConfigGuiTab.DEBUG){
            configs = Configs.Debug.DEFAULT_OPTIONS;
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
        ADMIN         ("duck.gui.button.config_gui.admin"),
        DEBUG("duck.gui.button.config_gui.debug");

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
