package io.github.beeebea.fastmove.client;

import io.github.beeebea.fastmove.IFastMoveInput;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FastMoveInput implements IFastMoveInput {
    private static KeyBinding moveUpKey =  KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.fastmove.up",
            InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.movement"));

    private static KeyBinding moveDownKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.fastmove.down",
            InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.movement"));
    boolean moveUpKeyPressed = false;
    boolean moveDownKeyPressed = false;
    boolean moveUpKeyPressedLastTick = false;
    boolean moveDownKeyPressedLastTick = false;

    public void onEndTick(MinecraftClient client) {
        if (client.player != null) {
            moveUpKeyPressedLastTick = moveUpKeyPressed;
            moveDownKeyPressedLastTick = moveDownKeyPressed;

            if (moveUpKey.isUnbound()) moveUpKeyPressed = client.player.input.jumping;
            else {
                moveUpKeyPressed = moveUpKey.isPressed();
                while (moveUpKey.wasPressed()){
                    moveUpKeyPressed = true;
                }
            }

            if (moveDownKey.isUnbound()) moveDownKeyPressed = client.player.input.sneaking;
            else {
                moveDownKeyPressed = moveDownKey.isPressed();
                while (moveDownKey.wasPressed()){
                    moveDownKeyPressed = true;
                }
            }
        }
    }

    @Override
    public boolean ismoveUpKeyPressed() {
        return moveUpKeyPressed;
    }

    @Override
    public boolean ismoveDownKeyPressed() {
        return moveDownKeyPressed;
    }

    @Override
    public boolean ismoveUpKeyPressedLastTick() {
        return moveUpKeyPressedLastTick;
    }

    @Override
    public boolean ismoveDownKeyPressedLastTick() {
        return moveDownKeyPressedLastTick;
    }
}
