package io.github.beeebea.fastmove;

import io.github.beeebea.fastmove.config.FastMoveConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.uku3lig.ukulib.config.ConfigManager;
import net.uku3lig.ukulib.config.serialization.TomlConfigSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class FastMove implements ModInitializer {
    public static final String MOD_ID = "fastmove";
    protected static FastMoveConfig serverConfig = null;
    public static FastMoveConfig getConfig() {
        if(serverConfig != null) return serverConfig;
        return FastMoveConfig.getManager().getConfig();
    }
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier MOVE_STATE = new Identifier(MOD_ID, "move_state");
    public static final Identifier CONFIG_STATE = new Identifier(MOD_ID, "config_state");
    private static final Object _queueLock = new Object();
    private static final Queue<Runnable> _actionQueue = new LinkedList<>();
    public static IMoveStateUpdater moveStateUpdater;
    public static IFastMoveInput INPUT;
    @Override
    public void onInitialize() {
        LOGGER.info("initializing FastMove :3");

        moveStateUpdater = new IMoveStateUpdater(){
            @Override
            public void setMoveState(PlayerEntity player, MoveState moveState) {}
            @Override
            public void setAnimationState(PlayerEntity player, MoveState moveState) {}
        };

        INPUT = new IFastMoveInput() {
            @Override
            public boolean ismoveUpKeyPressed() {return false;}
            @Override
            public boolean ismoveDownKeyPressed() {return false;}
            @Override
            public boolean ismoveUpKeyPressedLastTick() {return false;}
            @Override
            public boolean ismoveDownKeyPressedLastTick() {return false;}
        };

        ServerPlayNetworking.registerGlobalReceiver(MOVE_STATE, (server, player, handler, buf, responseSender) -> {
            var uuid = buf.readUuid();
            var moveStateInt = buf.readInt();
            MoveState moveState = MoveState.STATE(moveStateInt);
            IFastPlayer fastPlayer = (IFastPlayer) server.getPlayerManager().getPlayer(uuid);
            if( fastPlayer != null) fastPlayer.fastmove_setMoveState(moveState);

            SendToClients((PlayerEntity) fastPlayer, MOVE_STATE, uuid, moveStateInt);

        });

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            synchronized (_queueLock) {
                while (_actionQueue.size() > 0) {
                    _actionQueue.poll().run();
                }
            }
        });
        //send config to clients on join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            var buf = PacketByteBufs.create();
            buf.writeBoolean(getConfig().enableFastMove);
            buf.writeBoolean(getConfig().diveRollEnabled);
            buf.writeInt(getConfig().diveRollStaminaCost);
            buf.writeDouble(getConfig().diveRollSpeedBoostMultiplier);
            buf.writeInt(getConfig().diveRollCoolDown);
            buf.writeBoolean(getConfig().diveRollWhenSwimming);
            buf.writeBoolean(getConfig().diveRollWhenFlying);
            buf.writeBoolean(getConfig().wallRunEnabled);
            buf.writeInt(getConfig().wallRunStaminaCost);
            buf.writeDouble(getConfig().wallRunSpeedBoostMultiplier);
            buf.writeInt(getConfig().wallRunDurationTicks);
            buf.writeBoolean(getConfig().slideEnabled);
            buf.writeInt(getConfig().slideStaminaCost);
            buf.writeDouble(getConfig().slideSpeedBoostMultiplier);
            buf.writeInt(getConfig().slideCoolDown);
            sender.sendPacket(CONFIG_STATE, buf);
        });

    }

    public static void SendToClients(PlayerEntity source, Identifier type, UUID uuid, int moveStateInt){
        synchronized (_queueLock) {
            _actionQueue.add(() -> {
                for (PlayerEntity target : source.getServer().getPlayerManager().getPlayerList()) {
                    if (target != source && target.squaredDistanceTo(source) < 6400) {
                        var buf = PacketByteBufs.create();
                        buf.writeUuid(uuid);
                        buf.writeInt(moveStateInt);
                        ServerPlayNetworking.send((ServerPlayerEntity) target, type, buf);
                    }
                }
            });
        }
    }

}
