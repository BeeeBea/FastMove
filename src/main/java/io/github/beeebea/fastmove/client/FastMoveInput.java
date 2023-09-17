package io.github.beeebea.fastmove.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FastMoveInput {
    private static KeyBinding moveUpKey =  KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.fastmove.up",
            InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.movement"));

    private static KeyBinding moveDownKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.fastmove.down",
            InputUtil.Type.KEYSYM,GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.movement"));
    public boolean ismoveUpKeyPressed = false;
    public boolean ismoveDownKeyPressed = false;
    public boolean ismoveUpKeyPressedLastTick = false;
    public boolean ismoveDownKeyPressedLastTick = false;

    public void onEndTick(MinecraftClient client) {
        if (client.player != null) {
            ismoveUpKeyPressedLastTick = ismoveUpKeyPressed;
            ismoveDownKeyPressedLastTick = ismoveDownKeyPressed;

            if (moveUpKey.isUnbound()) ismoveUpKeyPressed = client.player.input.jumping;
            else {
                ismoveUpKeyPressed = moveUpKey.isPressed();
                while (moveUpKey.wasPressed()){
                    ismoveUpKeyPressed = true;
                }
            }

            if (moveDownKey.isUnbound()) ismoveDownKeyPressed = client.player.input.sneaking;
            else {
                ismoveDownKeyPressed = moveDownKey.isPressed();
                while (moveDownKey.wasPressed()){
                    ismoveDownKeyPressed = true;
                }
            }
        }
    }
}
