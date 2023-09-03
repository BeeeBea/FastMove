package io.github.beeebea.fastmove.mixin;

import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import io.github.beeebea.fastmove.IFastPlayer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Shadow public Input input;

    @Inject(method = "move" , at = @At("HEAD"))
    public void fastmove_move(MovementType type, Vec3d movement, CallbackInfo ci){
        if(((Object) this) instanceof IFastPlayer player){
            if(input != null) player.fastmove_setJumpInput(input.jumping);
        }

    }
}
