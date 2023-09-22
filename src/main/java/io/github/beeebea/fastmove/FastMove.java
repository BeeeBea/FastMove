package io.github.beeebea.fastmove;

import io.github.beeebea.fastmove.client.FastMoveInput;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.uku3lig.ukulib.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class FastMove implements ModInitializer {
    public static final String MOD_ID = "fastmove";
    public static final ConfigManager<FastMoveConfig> CONFIG_MANAGER =  ConfigManager.createDefault(FastMoveConfig.class, MOD_ID);
    public static FastMoveConfig getConfig() {
        return CONFIG_MANAGER.getConfig();
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier MOVE_STATE = new Identifier(MOD_ID, "move_state");
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
