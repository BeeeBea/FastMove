package io.github.beeebea.fastmove;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = FastMove.MOD_ID)
@Config(name = "fastmove", wrapperName = "FastMoveConfig")
public class FastMoveConfigModel {

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean enableFastMove = true;

    @Nest
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public movementOptionsEnv diveRoll = new movementOptionsEnv();
    @Nest
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public movementOptions slide = new movementOptions();
    @Nest
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public movementOptionsDuration wallRun = new movementOptionsDuration();

    public static class movementOptions {
        public boolean enabled = true;
        @RangeConstraint(min = 0, max = 999)
        public int staminaCost = 10;
        @RangeConstraint(min = 0, max = 10)
        public double speedBoostMult = 1.0;
    }
    public static class movementOptionsEnv {
        public boolean enabled = true;
        @RangeConstraint(min = 0, max = 999)
        public int staminaCost = 50;
        @RangeConstraint(min = 0, max = 10)
        public double speedBoostMult = 1.0;
        public boolean whenSwimming = false;
        public boolean whenFlying = false;
    }
    public static class movementOptionsDuration {
        public boolean enabled = true;
        @RangeConstraint(min = 0, max = 999)
        public int staminaCost = 0;
        @RangeConstraint(min = 0, max = 10)
        public double speedBoostMult = 1.0;
        @RangeConstraint(min = 1, max = 9999)
        public int durationTicks = 60;
    }
}
