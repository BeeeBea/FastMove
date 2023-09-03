package io.github.beeebea.fastmove;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Sync;

@Modmenu(modId = FastMove.MOD_ID)
@Config(name = "fastmove-config", wrapperName = "FastMoveConfig")
public class FastMoveConfigModel {
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public int wallRunDurationTicks = 60;
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public int rollStaminaCost = 50;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public double rollSpeedBoostMult = 1.0;
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public double slideSpeedBoostMult = 1.0;
}
