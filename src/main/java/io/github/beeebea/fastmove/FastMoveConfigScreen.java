package io.github.beeebea.fastmove;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;
import net.uku3lig.ukulib.config.ConfigManager;
import net.uku3lig.ukulib.config.option.CyclingOption;
import net.uku3lig.ukulib.config.option.WidgetCreator;
import net.uku3lig.ukulib.config.screen.AbstractConfigScreen;

public class FastMoveConfigScreen extends AbstractConfigScreen<FastMoveConfig> {

    /**
     * Creates a config screen.
     *
     * @param parent  The parent screen
     * @param manager The config manager
     */
    protected FastMoveConfigScreen(Screen parent, ConfigManager<FastMoveConfig> manager) {
        super("FastMove Config", parent, manager);
    }


    @Override
    protected WidgetCreator[] getWidgets(FastMoveConfig config) {
        return new WidgetCreator[]{
            CyclingOption.ofBoolean("text.config.fastmove.option.enableFastMove",config.enableFastMove, (enableFastMove) -> config.enableFastMove = enableFastMove)
        };
    }


}
