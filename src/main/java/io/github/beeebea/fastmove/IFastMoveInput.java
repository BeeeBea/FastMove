package io.github.beeebea.fastmove;

import io.github.beeebea.fastmove.client.FastMoveInput;

public interface IFastMoveInput {
    boolean ismoveUpKeyPressed();
    boolean ismoveDownKeyPressed();
    boolean ismoveUpKeyPressedLastTick();
    boolean ismoveDownKeyPressedLastTick();
}
