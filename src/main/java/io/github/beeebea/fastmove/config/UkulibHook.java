package io.github.beeebea.fastmove.config;

import io.github.beeebea.fastmove.client.FastMoveClient;
import net.minecraft.client.gui.screen.Screen;
import net.uku3lig.ukulib.api.UkulibAPI;

import java.util.function.UnaryOperator;

public class UkulibHook implements UkulibAPI {
    @Override
    public UnaryOperator<Screen> supplyConfigScreen() {
        return parent -> new FastMoveConfigScreen(parent, FastMoveClient.CONFIG_MANAGER);
    }
}