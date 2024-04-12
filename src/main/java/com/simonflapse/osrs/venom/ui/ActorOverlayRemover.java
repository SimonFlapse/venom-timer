package com.simonflapse.osrs.venom.ui;

import net.runelite.api.Actor;
import net.runelite.client.ui.overlay.Overlay;

public interface ActorOverlayRemover {
    void removeActorOverlay(Actor actor, Overlay overlay);
}
