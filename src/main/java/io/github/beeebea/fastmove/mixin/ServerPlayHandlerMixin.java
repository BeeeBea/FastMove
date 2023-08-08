package io.github.beeebea.fastmove.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayHandlerMixin {

    @ModifyVariable(at = @At("STORE"), method = "onPlayerMove", ordinal = 2)
    private boolean modifyCheatDetector(boolean var){
        return false;
    }

}