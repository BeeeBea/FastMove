package io.github.beeebea.fastmove;

import io.github.beeebea.fastmove.client.FastMoveInput;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import io.github.beeebea.fastmove.FastMoveConfig;

public class FastMove implements ModInitializer {
    public static final String MOD_ID = "fastmove";
    public static final FastMoveConfig CONFIG = FastMoveConfig.createAndLoad();
    public static FastMoveInput INPUT = new FastMoveInput();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier MOVE_STATE = new Identifier(MOD_ID, "move_state");
    private static final Object _queueLock = new Object();
    private static final Queue<Runnable> _actionQueue = new LinkedList<>();
    public static IMoveStateUpdater moveStateUpdater;

    @Override
    public void onInitialize() {
        LOGGER.info("initializing FastMove :3");

        moveStateUpdater = new IMoveStateUpdater(){
            @Override
            public void setMoveState(PlayerEntity player, MoveState moveState) {
            }
            @Override
            public void setAnimationState(PlayerEntity player, MoveState moveState) {

            }
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
