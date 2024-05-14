package com.simonflapse.osrs.venom.ui;

import com.simonflapse.osrs.venom.VenomTimerConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.client.game.NPCManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Singleton
public class OverlayOrchestrator {
    private final ConcurrentHashMap<Actor, VenomTimerOverlay> activeOverlays = new ConcurrentHashMap<>();
    private final OverlayManager overlayManager;
    private final VenomTimerConfig config;
    private final NPCManager npcManager;

    @Inject
    public OverlayOrchestrator(OverlayManager overlayManager, VenomTimerConfig config, NPCManager npcManager) {
        this.overlayManager = overlayManager;
        this.config = config;
        this.npcManager = npcManager;
    }

    public void updateDamage(Actor actor, int damage) {
        VenomTimerOverlay actorOverlay = activeOverlays.get(actor);
        if (actorOverlay == null) {
            actorOverlay = initializeNewOverlay(actor);
        }

        actorOverlay.updateVenom(damage);
    }

    private VenomTimerOverlay initializeNewOverlay(Actor actor) {
        VenomTimerOverlay actorOverlay = new VenomTimerOverlay(config, npcManager, actor, this::removeActorOverlay);
        overlayManager.add(actorOverlay);
        activeOverlays.put(actor, actorOverlay);
        return actorOverlay;
    }

    private void removeActorOverlay(Actor actor, Overlay overlay) {
        log.debug("Overlay associated with: {} will be removed", getActorName(actor));
        activeOverlays.remove(actor);
        overlayManager.remove(overlay);
    }

    public void shutDown() {
        for (Overlay overlay : activeOverlays.values()) {
            overlayManager.remove(overlay);
        }
        activeOverlays.clear();
    }

    private static String getActorName(Actor actor) {
        String name = actor.getName();
        if (actor instanceof NPC) {
            name += "#" + ((NPC) actor).getId();
        }
        return name;
    }
}
