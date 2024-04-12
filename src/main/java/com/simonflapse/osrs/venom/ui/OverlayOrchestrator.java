package com.simonflapse.osrs.venom.ui;

import net.runelite.api.Actor;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class OverlayOrchestrator {
    private final ConcurrentHashMap<Actor, VenomTimerOverlay> activeOverlays = new ConcurrentHashMap<>();
    private final OverlayManager overlayManager;

    @Inject
    public OverlayOrchestrator(OverlayManager overlayManager) {
        this.overlayManager = overlayManager;
    }

    public void updateDamage(Actor actor, int damage) {
        VenomTimerOverlay actorOverlay = activeOverlays.get(actor);
        if (actorOverlay == null) {
            actorOverlay = new VenomTimerOverlay(actor, this::removeActorOverlay);
            overlayManager.add(actorOverlay);
            activeOverlays.put(actor, actorOverlay);
        }

        actorOverlay.updateVenom(damage);
    }

    private void removeActorOverlay(Actor actor, Overlay overlay) {
        System.out.println("Overlay associated with: " + actor.getName() + " will be removed");
        activeOverlays.remove(actor);
        overlayManager.remove(overlay);
    }

    public void shutDown() {
        for (Overlay overlay : activeOverlays.values()) {
            overlayManager.remove(overlay);
        }
    }
}
