package io.github.beeebea.fastmove;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;

public interface IAnimatedPlayer {

    ModifierLayer<IAnimation> fastmove_getModAnimation();
    ModifierLayer<IAnimation> fastmove_getModAnimationBody();


}
