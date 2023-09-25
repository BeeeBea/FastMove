package io.github.beeebea.fastmove.config;

import io.github.beeebea.fastmove.FastMove;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.uku3lig.ukulib.config.ConfigManager;


import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
public class FastMoveConfig implements Serializable {
    @Getter
    private static final ConfigManager<FastMoveConfig> manager = ConfigManager.createDefault(FastMoveConfig.class, FastMove.MOD_ID);

    public FastMoveConfig() {
        enableFastMove = true;
        diveRollEnabled = true;
        diveRollStaminaCost = 50;
        diveRollSpeedBoostMultiplier = 1;
        diveRollCoolDown = 0;
        diveRollWhenSwimming = false;
        diveRollWhenFlying = false;
        wallRunEnabled = true;
        wallRunStaminaCost = 0;
        wallRunSpeedBoostMultiplier = 1;
        wallRunDurationTicks = 60;
        slideEnabled = true;
        slideStaminaCost = 10;
        slideSpeedBoostMultiplier = 1;
        slideCoolDown = 0;

    }
    
    public boolean enableFastMove;

    public boolean diveRollEnabled;
    public int diveRollStaminaCost;
    public double diveRollSpeedBoostMultiplier;
    public int diveRollCoolDown;
    public boolean diveRollWhenSwimming;
    public boolean diveRollWhenFlying;

    public boolean wallRunEnabled;
    public int wallRunStaminaCost;
    public double wallRunSpeedBoostMultiplier;
    public int wallRunDurationTicks;

    public boolean slideEnabled;
    public int slideStaminaCost;
    public double slideSpeedBoostMultiplier;
    public int slideCoolDown;

    public FastMoveConfig(FastMoveConfig config) {
        enableFastMove = config.enableFastMove;
        diveRollEnabled = config.diveRollEnabled;
        diveRollStaminaCost = config.diveRollStaminaCost;
        diveRollSpeedBoostMultiplier = config.diveRollSpeedBoostMultiplier;
        diveRollCoolDown = config.diveRollCoolDown;
        diveRollWhenSwimming = config.diveRollWhenSwimming;
        diveRollWhenFlying = config.diveRollWhenFlying;
        wallRunEnabled = config.wallRunEnabled;
        wallRunStaminaCost = config.wallRunStaminaCost;
        wallRunSpeedBoostMultiplier = config.wallRunSpeedBoostMultiplier;
        wallRunDurationTicks = config.wallRunDurationTicks;
        slideEnabled = config.slideEnabled;
        slideStaminaCost = config.slideStaminaCost;
        slideSpeedBoostMultiplier = config.slideSpeedBoostMultiplier;
        slideCoolDown = config.slideCoolDown;
    }
}
