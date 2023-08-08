package io.github.beeebea.fastmove;

public interface IFastPlayer {
    MoveState fastmove_getMoveState();
    void fastmove_setMoveState(MoveState moveState);
    void fastmove_setJumpInput(boolean input);

}
