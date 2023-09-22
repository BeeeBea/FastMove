package io.github.beeebea.fastmove.config;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.uku3lig.ukulib.config.ConfigManager;
import net.uku3lig.ukulib.config.option.*;
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
                CyclingOption.ofBoolean("text.config.fastmove.option.enableFastMove", config.enableFastMove,  config::setEnableFastMove),

                new TextOption("text.config.fastmove.category.diveRoll", true).wide(),
                CyclingOption.ofBoolean("text.config.fastmove.option.diveRoll.enabled", config.diveRollEnabled, config::setDiveRollEnabled).wide(),
                new IntSliderOption("text.config.fastmove.option.diveRoll.staminaCost", config.getDiveRollStaminaCost(), config::setDiveRollStaminaCost, IntSliderOption.DEFAULT_INT_TO_TEXT,0,256),
                new IntSliderOption("text.config.fastmove.option.diveRoll.cooldown", config.getDiveRollCoolDown(), config::setDiveRollCoolDown, IntSliderOption.DEFAULT_INT_TO_TEXT,0,256),
                new SliderOption("text.config.fastmove.option.diveRoll.speedBoostMult", config.getDiveRollSpeedBoostMultiplier(), config::setDiveRollSpeedBoostMultiplier, SliderOption.PERCENT_VALUE_TO_TEXT,0,3),
                CyclingOption.ofBoolean("text.config.fastmove.option.diveRoll.whenSwimming", config.diveRollWhenSwimming, config::setDiveRollWhenSwimming),
                CyclingOption.ofBoolean("text.config.fastmove.option.diveRoll.whenFlying", config.diveRollWhenFlying, config::setDiveRollWhenFlying),

                new TextOption("text.config.fastmove.category.slide", true).wide(),
                CyclingOption.ofBoolean("text.config.fastmove.option.slide.enabled", config.slideEnabled, config::setSlideEnabled).wide(),
                new IntSliderOption("text.config.fastmove.option.slide.staminaCost", config.getSlideStaminaCost(), config::setSlideStaminaCost, IntSliderOption.DEFAULT_INT_TO_TEXT,0,256),
                new IntSliderOption("text.config.fastmove.option.slide.cooldown", config.getSlideCoolDown(), config::setSlideCoolDown, IntSliderOption.DEFAULT_INT_TO_TEXT,0,256),
                new SliderOption("text.config.fastmove.option.slide.speedBoostMult", config.getSlideSpeedBoostMultiplier(), config::setSlideSpeedBoostMultiplier, SliderOption.PERCENT_VALUE_TO_TEXT,0,3),

                new TextOption("text.config.fastmove.category.wallRun", true).wide(),
                CyclingOption.ofBoolean("text.config.fastmove.option.wallRun.enabled", config.wallRunEnabled, config::setWallRunEnabled).wide(),
                new IntSliderOption("text.config.fastmove.option.wallRun.staminaCost", config.getWallRunStaminaCost(), config::setWallRunStaminaCost, IntSliderOption.DEFAULT_INT_TO_TEXT,0,256),
                new IntSliderOption("text.config.fastmove.option.wallRun.durationTicks", config.getWallRunDurationTicks(), config::setWallRunDurationTicks, IntSliderOption.DEFAULT_INT_TO_TEXT,1,256),
                new SliderOption("text.config.fastmove.option.wallRun.speedBoostMult", config.getWallRunSpeedBoostMultiplier(), config::setWallRunSpeedBoostMultiplier, SliderOption.PERCENT_VALUE_TO_TEXT,0,3),
        };
    }


}
