package io.github.beeebea.fastmove.client;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import io.github.beeebea.fastmove.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Deque;
import java.util.Map;
import java.util.Objects;

public class FastMoveClient extends FastMove implements ClientModInitializer {
    private static final Map<String, KeyframeAnimation> _animations = new java.util.HashMap<>();


    @Override
    public void onInitializeClient() {
        LOGGER.info("initializing FastMove Client :3");

        moveStateUpdater = new IMoveStateUpdater(){
            @Override
            public void setMoveState(PlayerEntity player, MoveState moveState) {
                var buf = PacketByteBufs.create();
                buf.writeUuid(player.getUuid());
                buf.writeInt(MoveState.STATE(moveState));
                ClientPlayNetworking.send(FastMove.MOVE_STATE, buf);
            }
            @Override
            public void setAnimationState(PlayerEntity player, MoveState moveState) {
                if(!((player) instanceof IAnimatedPlayer animatedPlayer)) return;
                //LOGGER.info("found IAnimatedPlayer");
                var animationContainer = animatedPlayer.fastmove_getModAnimation();
                var animationBodyContainer = animatedPlayer.fastmove_getModAnimationBody();
                if(animationContainer == null || animationBodyContainer == null) return;
                //LOGGER.info("found animationContainers");

                if(_animations.isEmpty()){
                    //LOGGER.info("loading animations");
                    for(var entry : MoveState.STATES.values()){
                        //LOGGER.info("trying animation: " + entry.name);
                        var name = entry.name;
                        if(name.equals("none")) continue;
                        KeyframeAnimation animation = PlayerAnimationRegistry.getAnimation(new Identifier(MOD_ID, entry.name));
                        if(animation == null) continue;
                        //LOGGER.info("found animation: " + entry.name);
                        _animations.put(entry.name, animation);
                    }
                }

                var fade = AbstractFadeModifier.standardFadeIn(1, Ease.INOUTQUAD);
                var anim = _animations.get(moveState.name);
                if(anim == null) {
                    animationBodyContainer.replaceAnimationWithFade(fade, null);
                    animationContainer.replaceAnimationWithFade(fade, null);
                    return;
                }
                //LOGGER.info("found animation");
                var bodyLayer = new KeyframeAnimationPlayer(anim);
                var bodyVal = bodyLayer.bodyParts.get("body");
                bodyLayer.bodyParts.clear();
                bodyLayer.bodyParts.put("body", bodyVal);
                animationBodyContainer.replaceAnimationWithFade(fade, bodyLayer);
                animationContainer.replaceAnimationWithFade(fade, new KeyframeAnimationPlayer(anim));
            }
        };

        //register receivers
        ClientPlayNetworking.registerGlobalReceiver(FastMove.MOVE_STATE, (client, handler, buf, responseSender) -> {
            if (client.world != null) {
                var uuid = buf.readUuid();
                var moveStateInt = buf.readInt();
                MoveState moveState = MoveState.STATE(moveStateInt);
                IFastPlayer fastPlayer = (IFastPlayer) client.world.getPlayerByUuid(uuid);
                if (fastPlayer != null) fastPlayer.fastmove_setMoveState(moveState);
            }
        });

    }

}
