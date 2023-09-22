package io.github.beeebea.fastmove;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.uku3lig.ukulib.config.ConfigManager;


import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FastMoveConfig implements Serializable {
    @Getter
    private static final ConfigManager<FastMoveConfig> manager = ConfigManager.createDefault(FastMoveConfig.class, FastMove.MOD_ID);

    public FastMoveConfig defaultConfig() {
        return new FastMoveConfig(true, new movementOptionsEnv(), new movementOptions(), new movementOptionsDuration());
    }
    
    public boolean enableFastMove = true;
    public movementOptionsEnv diveRoll = new movementOptionsEnv();
    public movementOptions slide = new movementOptions();
    public movementOptionsDuration wallRun = new movementOptionsDuration();

    public static class movementOptions {
        public boolean enabled = true;
        //@RangeConstraint(min = 0, max = 999)
        public int staminaCost = 10;
        // @RangeConstraint(min = 0, max = 10)
        public double speedBoostMult = 1.0;
        // @RangeConstraint(min = 0, max = 999)
        public int cooldown = 0;
    }
    public static class movementOptionsEnv {
        public boolean enabled = true;
        // @RangeConstraint(min = 0, max = 999)
        public int staminaCost = 50;
        //@RangeConstraint(min = 0, max = 10)
        public double speedBoostMult = 1.0;
        //@RangeConstraint(min = 0, max = 999)
        public int cooldown = 0;
        public boolean whenSwimming = false;
        public boolean whenFlying = false;
    }
    public static class movementOptionsDuration {
        public boolean enabled = true;
        //@RangeConstraint(min = 0, max = 999)
        public int staminaCost = 0;
        //@RangeConstraint(min = 0, max = 10)
        public double speedBoostMult = 1.0;
        // @RangeConstraint(min = 1, max = 9999)
        public int durationTicks = 60;
    }
}
